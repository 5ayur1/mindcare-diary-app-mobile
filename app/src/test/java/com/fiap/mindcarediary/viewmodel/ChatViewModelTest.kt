package com.fiap.mindcarediary.viewmodel

import com.fiap.mindcarediary.repository.ChatRepository
import com.fiap.mindcarediary.service.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @Test fun fallbackIsVisibleAndRetryReplacesItWithoutSavingAssistantText() = runTest {
        val repo = FakeRepository(); repo.fallback = true
        val model = ChatViewModel(repo, this)
        model.updateInput("Meu relato"); model.send(); advanceUntilIdle()
        assertEquals(3, model.state.value.messages.size)
        assertEquals("Gostaria de contar mais?", model.state.value.messages.last().text)
        assertNotNull(model.state.value.pendingReply)
        model.retryReply(); advanceUntilIdle()
        assertEquals(3, model.state.value.messages.size)
        repo.fallback = false; model.retryReply(); advanceUntilIdle()
        assertEquals(3, model.state.value.messages.size)
        assertNull(model.state.value.pendingReply)
        assertNull(model.state.value.error)
        model.review()
        assertEquals("Meu relato", model.state.value.confirmedText)
        assertTrue(repo.saves.isEmpty())
    }

    @Test fun speechAppendsToDraftAndOnlySendsAfterExplicitReviewAndSend() = runTest {
        val repo = FakeRepository(); val model = ChatViewModel(repo, this)
        model.updateInput("Hoje")
        assertTrue(model.beginSpeech())
        assertFalse(model.beginSpeech())
        model.send(); model.updateInput("substituição indevida")
        model.finishSpeech(" fui caminhar "); advanceUntilIdle()
        assertEquals("Hoje fui caminhar", model.state.value.input)
        assertEquals(1, model.state.value.messages.size)
        assertTrue(repo.sends.isEmpty()); assertTrue(repo.saves.isEmpty())
        model.updateInput("Hoje fui caminhar no parque"); model.send(); advanceUntilIdle()
        assertEquals(listOf("Hoje fui caminhar no parque"), repo.sends)
    }

    @Test fun speechCancellationAndFailurePreserveTypedDraftAndAllowRetry() = runTest {
        val model = ChatViewModel(FakeRepository(), this)
        model.updateInput("Rascunho")
        model.beginSpeech(); model.finishSpeech()
        assertEquals("Rascunho", model.state.value.input)
        assertNull(model.state.value.error)
        assertTrue(model.beginSpeech())
        model.finishSpeech(error = "Serviço indisponível")
        assertEquals("Rascunho", model.state.value.input)
        assertNotNull(model.state.value.error)
        assertFalse(model.state.value.recognizingSpeech)
        assertTrue(model.beginSpeech())
        model.finishSpeech(" ", "Fala não reconhecida")
        assertEquals("Rascunho", model.state.value.input)
    }

    @Test fun overlongSpeechDoesNotTruncateOrOverwriteDraft() = runTest {
        val model = ChatViewModel(FakeRepository(), this)
        model.updateInput("a".repeat(3998))
        model.beginSpeech(); model.finishSpeech("bb")
        assertEquals("a".repeat(3998), model.state.value.input)
        assertNotNull(model.state.value.error)
        model.beginSpeech(); model.finishSpeech("b")
        assertEquals(4000, model.state.value.input.length)
        assertNull(model.state.value.error)
    }

    @Test fun speechIsBlockedDuringReplyAndReviewAndAfterSave() = runTest {
        val repo = FakeRepository(); repo.waitSend = CompletableDeferred()
        val model = ChatViewModel(repo, this)
        model.updateInput("Relato"); model.send(); runCurrent()
        assertFalse(model.beginSpeech())
        repo.waitSend!!.complete(Unit); advanceUntilIdle()
        assertTrue(model.beginSpeech()); model.review()
        assertFalse(model.state.value.reviewing)
        model.finishSpeech(); model.review()
        assertFalse(model.beginSpeech())
        model.save(); advanceUntilIdle()
        assertFalse(model.beginSpeech())
        model.finishSpeech("resultado tardio")
        assertEquals("", model.state.value.input)
    }

    @Test fun pendingReplyPreventsSpeechUntilResolved() = runTest {
        val repo = FakeRepository(); repo.failSend = true
        val model = ChatViewModel(repo, this)
        model.updateInput("Relato"); model.send(); advanceUntilIdle()
        assertFalse(model.beginSpeech())
        model.finishSpeech("resultado inesperado")
        assertEquals("", model.state.value.input)
    }

    private class FakeRepository : ChatRepository {
        var failSend = false
        var fallback = false
        var failSave = false
        var waitSend: CompletableDeferred<Unit>? = null
        val sends = mutableListOf<String>()
        val saves = mutableListOf<MiaRegistroRequest>()
        override suspend fun send(message: String): MiaMessageResponse {
            sends += message
            waitSend?.await()
            if (failSend) error("erro sensível do servidor")
            return MiaMessageResponse("MIA", "Assistente de registro", "Gostaria de contar mais?", fallback)
        }
        override suspend fun save(request: MiaRegistroRequest): RegistroDiario {
            saves += request
            if (failSave) error("erro de rede")
            return RegistroDiario(request.nivelHumor, "", "", "2026-09-17T12:00:00", 1L, request.textoConfirmado, "CHAT")
        }
    }

    @Test fun reviewContainsOnlyPatientMessagesAndNeverSavesBeforeConfirmation() = runTest {
        val repo = FakeRepository(); val model = ChatViewModel(repo, this)
        model.updateInput("Meu primeiro relato"); model.send(); advanceUntilIdle()
        model.updateInput("Meu segundo relato"); model.send(); advanceUntilIdle()
        model.review()
        assertEquals("Meu primeiro relato\n\nMeu segundo relato", model.state.value.confirmedText)
        assertTrue(repo.saves.isEmpty())
        model.editReview("Texto revisado por mim"); model.selectMood("BOM"); model.save(); advanceUntilIdle()
        assertEquals("Texto revisado por mim", repo.saves.single().textoConfirmado)
        assertEquals("BOM", repo.saves.single().nivelHumor)
        assertTrue(model.state.value.saved)
        assertTrue(model.state.value.messages.isEmpty())
        assertEquals("", model.state.value.confirmedText)
    }

    @Test fun failedReplyCanBeRetriedWithoutDuplicatingPatientMessage() = runTest {
        val repo = FakeRepository(); repo.failSend = true
        val model = ChatViewModel(repo, this)
        model.updateInput("Meu dia"); model.send(); advanceUntilIdle()
        assertNotNull(model.state.value.error)
        assertFalse(model.state.value.error!!.contains("sensível"))
        repo.failSend = false; model.retryReply(); advanceUntilIdle()
        assertEquals(1, model.state.value.messages.count { it.patient })
        assertNull(model.state.value.pendingReply)
        assertEquals(2, repo.sends.size)
    }

    @Test fun fallbackStillAllowsReviewAndSave() = runTest {
        val repo = FakeRepository(); repo.fallback = true
        val model = ChatViewModel(repo, this)
        model.updateInput("Relato importante"); model.send(); advanceUntilIdle(); model.review()
        assertEquals("Relato importante", model.state.value.confirmedText)
        model.save(); advanceUntilIdle()
        assertTrue(model.state.value.saved)
    }

    @Test fun uncertainSaveFreezesPayloadAndReusesIdempotencyKey() = runTest {
        val repo = FakeRepository(); repo.failSave = true
        val model = ChatViewModel(repo, this)
        model.updateInput("Relato original"); model.send(); advanceUntilIdle(); model.review(); model.save(); advanceUntilIdle()
        assertTrue(model.state.value.saveAttempted)
        model.editReview("Outro texto"); model.selectMood("PESSIMO"); model.cancelReview()
        assertTrue(model.state.value.reviewing)
        assertEquals("Relato original", model.state.value.confirmedText)
        repo.failSave = false; model.save(); advanceUntilIdle()
        assertEquals(repo.saves[0], repo.saves[1])
        model.save(); advanceUntilIdle()
        assertEquals(2, repo.saves.size)
    }

    @Test fun blocksEmptySendDoubleSendAndReviewWhileSending() = runTest {
        val repo = FakeRepository(); repo.waitSend = CompletableDeferred()
        val model = ChatViewModel(repo, this)
        model.updateInput(" "); model.send(); advanceUntilIdle(); assertTrue(repo.sends.isEmpty())
        model.updateInput("Relato"); model.send(); model.send(); model.review(); runCurrent()
        assertFalse(model.state.value.reviewing)
        assertEquals(1, repo.sends.size)
        repo.waitSend!!.complete(Unit); advanceUntilIdle()
        model.review(); assertTrue(model.state.value.reviewing)
    }

    @Test fun doesNotSilentlyDiscardUnsentTextWhenReviewing() = runTest {
        val repo = FakeRepository(); val model = ChatViewModel(repo, this)
        model.updateInput("Enviado"); model.send(); advanceUntilIdle()
        model.updateInput("Ainda não enviado"); model.review()
        assertFalse(model.state.value.reviewing)
        assertEquals("Ainda não enviado", model.state.value.input)
        assertNotNull(model.state.value.error)
    }

    @Test fun enforcesMessageAndRecordLimits() = runTest {
        val repo = FakeRepository(); val model = ChatViewModel(repo, this)
        model.updateInput("a".repeat(4001)); assertEquals("", model.state.value.input)
        repeat(4) { model.updateInput("a".repeat(4000)); model.send(); advanceUntilIdle() }
        model.updateInput("a".repeat(4000)); model.send(); advanceUntilIdle()
        assertNotNull(model.state.value.error)
        assertEquals(4, repo.sends.size)
    }

    @Test fun keepsReviewedEditsWhenReturningWithoutNewMessages() = runTest {
        val model = ChatViewModel(FakeRepository(), this)
        model.updateInput("Original"); model.send(); advanceUntilIdle(); model.review()
        model.editReview("Revisado"); model.cancelReview(); model.review()
        assertEquals("Revisado", model.state.value.confirmedText)
    }
}
