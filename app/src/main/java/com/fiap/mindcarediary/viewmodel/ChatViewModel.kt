package com.fiap.mindcarediary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.ApiChatRepository
import com.fiap.mindcarediary.repository.ChatRepository
import com.fiap.mindcarediary.service.MiaRegistroRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(val id: String = UUID.randomUUID().toString(), val text: String, val patient: Boolean) {
    override fun toString() = "ChatMessage[conteudo omitido]"
}

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(ChatMessage(text = "Como foi o seu dia hoje?", patient = false)),
    val input: String = "",
    val recognizingSpeech: Boolean = false,
    val sending: Boolean = false,
    val pendingReply: String? = null,
    val error: String? = null,
    val reviewing: Boolean = false,
    val confirmedText: String = "",
    val mood: String = "SEM_DEFINICAO",
    val saving: Boolean = false,
    val saveAttempted: Boolean = false,
    val saved: Boolean = false
) {
    val hasDraft get() = input.isNotBlank() || messages.any { it.patient }
    override fun toString() = "ChatUiState[conteudo omitido]"
}

class ChatViewModel(
    private val repository: ChatRepository = ApiChatRepository(),
    private val operationScope: CoroutineScope? = null
) : ViewModel() {
    private val scope get() = operationScope ?: viewModelScope
    private val mutableState = MutableStateFlow(ChatUiState())
    val state = mutableState.asStateFlow()
    private var saveRequest: MiaRegistroRequest? = null

    fun updateInput(text: String) {
        if (!state.value.recognizingSpeech && !state.value.sending && !state.value.reviewing && !state.value.saved && state.value.pendingReply == null && text.length <= 4000) {
            mutableState.update { it.copy(input = text, error = null) }
        }
    }

    fun beginSpeech(): Boolean {
        val current = state.value
        if (current.recognizingSpeech || current.sending || current.reviewing || current.saved || current.pendingReply != null) return false
        mutableState.update { it.copy(recognizingSpeech = true, error = null) }
        return true
    }

    // Recognition only edits the unsent draft; sending always requires a separate user action.
    fun finishSpeech(transcription: String? = null, error: String? = null) {
        if (!state.value.recognizingSpeech) return
        mutableState.update { current ->
            val text = transcription?.trim().orEmpty()
            val combined = if (text.isEmpty()) current.input else
                listOf(current.input.trimEnd(), text).filter { it.isNotEmpty() }.joinToString(" ")
            if (combined.length > 4000) {
                current.copy(recognizingSpeech = false, error = "A transcrição ultrapassa o limite de 4000 caracteres. Seu rascunho foi mantido. Dite um trecho menor.")
            } else {
                current.copy(recognizingSpeech = false, input = combined, error = error)
            }
        }
    }

    fun send() {
        val current = state.value
        val text = current.input.trim()
        if (current.recognizingSpeech || current.sending || current.reviewing || current.saved || current.pendingReply != null || text.isBlank()) return
        val draft = (current.messages.filter { it.patient }.map { it.text } + text).joinToString("\n\n")
        if (draft.length > 20000) {
            mutableState.update { it.copy(error = "O registro atingiu o limite de 20000 caracteres. Revise e salve antes de começar outro.") }
            return
        }
        val message = ChatMessage(text = text, patient = true)
        mutableState.update { it.copy(messages = it.messages + message, input = "", pendingReply = message.id, confirmedText = "", error = null) }
        retryReply()
    }

    fun retryReply() {
        val current = state.value
        if (current.sending || current.reviewing || current.saved) return
        val message = current.messages.find { it.id == current.pendingReply } ?: return
        mutableState.update { it.copy(sending = true, error = null) }
        scope.launch {
            try {
                val response = repository.send(message.text)
                // Replace the same response on retry, including a previously shown fallback.
                val replyId = "reply-${message.id}"
                val previousReplies = current.messages.filter { !it.patient && it.id != replyId }.map { it.text }
                val reply = ChatMessage(id = replyId,
                    text = MiaFollowUpPolicy.reply(response.mensagem, previousReplies, response.fallback), patient = false)
                if (response.fallback) {
                    mutableState.update { it.copy(messages = it.messages.filterNot { item -> item.id == replyId } + reply,
                        error = "Resposta limitada: tente novamente ou revise seu relato para salvar.") }
                } else {
                    mutableState.update { it.copy(messages = it.messages.filterNot { item -> item.id == replyId } + reply, pendingReply = null) }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(error = "Não foi possível obter a resposta. Seu relato continua no rascunho.") }
            } finally {
                mutableState.update { it.copy(sending = false) }
            }
        }
    }

    fun review() {
        val current = state.value
        if (current.recognizingSpeech || current.sending || current.saving || current.saved || current.reviewing) return
        if (current.input.isNotBlank()) {
            mutableState.update { it.copy(error = "Envie ou apague o texto do campo antes de revisar o registro.") }
            return
        }
        val text = current.messages.filter { it.patient }.joinToString("\n\n") { it.text }
        if (text.isBlank()) return
        mutableState.update { it.copy(reviewing = true, confirmedText = it.confirmedText.ifBlank { text }, error = null) }
    }

    fun editReview(text: String) {
        if (!state.value.saveAttempted && text.length <= 20000) mutableState.update { it.copy(confirmedText = text) }
    }

    fun selectMood(mood: String) {
        if (!state.value.saveAttempted && mood in listOf("SEM_DEFINICAO", "OTIMO", "BOM", "NEUTRO", "MAL", "PESSIMO")) {
            mutableState.update { it.copy(mood = mood) }
        }
    }

    fun cancelReview() {
        if (!state.value.saveAttempted) mutableState.update { it.copy(reviewing = false, error = null) }
    }

    fun save() {
        val current = state.value
        if (!current.reviewing || current.saving || current.saved || current.confirmedText.isBlank()) return
        // Keep the exact same key and body after an uncertain network result.
        val request = saveRequest ?: MiaRegistroRequest(UUID.randomUUID().toString(), current.confirmedText.trim(), current.mood)
            .also { saveRequest = it }
        mutableState.update { it.copy(saving = true, saveAttempted = true, error = null) }
        scope.launch {
            try {
                repository.save(request)
                saveRequest = null
                mutableState.value = ChatUiState(messages = emptyList(), saved = true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(saving = false, error = "Não foi possível confirmar o salvamento. Tente novamente com este mesmo registro ou consulte seu histórico antes de criar outro.") }
            }
        }
    }
}
