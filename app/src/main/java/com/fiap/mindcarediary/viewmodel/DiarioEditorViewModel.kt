package com.fiap.mindcarediary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.*
import com.fiap.mindcarediary.service.RegistroDiario
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class DiarioEditorState(val positivo: String = "", val negativo: String = "", val humor: String = "SEM_DEFINICAO",
    val pronto: Boolean = false, val salvando: Boolean = false, val salvo: Boolean = false, val erro: String? = null, val aviso: String? = null)

class DiarioEditorViewModel(private val submit: suspend (RegistroDiario, String) -> Unit = { r, email -> PacienteRepository().cadastrarRegistroDiario(r, email); Unit },
    private val testScope: CoroutineScope? = null) : ViewModel() {
    private val scope get() = testScope ?: viewModelScope
    private val mutable = MutableStateFlow(DiarioEditorState())
    val state = mutable.asStateFlow()
    private var store: DraftStore? = null
    private var account = ""
    private var writer: Job? = null
    fun bind(account: String, draft: DraftStore) {
        if (store != null) return
        this.account = account; store = draft
        scope.launch {
            try {
                draft.load()?.let { value ->
                    val old = Gson().fromJson(value, DiarioEditorState::class.java)
                    mutable.value = DiarioEditorState(old.positivo, old.negativo, old.humor, aviso = "Rascunho recuperado deste aparelho.")
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutable.value = state.value.copy(aviso = "Não foi possível recuperar o rascunho local.") }
            mutable.value = state.value.copy(pronto = true)
            writer = scope.launch {
                state.map { Triple(it.positivo, it.negativo, it.humor) }.distinctUntilChanged().collect {
                    try { persist() } catch (e: CancellationException) { throw e }
                    catch (_: Exception) { mutable.value = state.value.copy(aviso = "Não foi possível proteger o rascunho neste aparelho. Mantenha a tela aberta até salvar.") }
                }
            }
        }
    }
    private suspend fun persist() {
        val s = state.value
        store?.save(if (s.salvo || (s.positivo.isBlank() && s.negativo.isBlank() && s.humor == "SEM_DEFINICAO")) null else Gson().toJson(s.copy(erro = null, aviso = null)))
    }
    fun editar(positivo: String = state.value.positivo, negativo: String = state.value.negativo, humor: String = state.value.humor) {
        if (!state.value.pronto || state.value.salvando || state.value.salvo || positivo.length + negativo.length > 20000) return
        mutable.value = state.value.copy(positivo = positivo, negativo = negativo, humor = humor, erro = null)
    }
    fun descartar() { if (!state.value.salvando && state.value.pronto) mutable.value = DiarioEditorState(pronto = true) }
    fun salvar() {
        val s = state.value
        if (!s.pronto || s.salvando || s.salvo) return
        if (s.positivo.isBlank() && s.negativo.isBlank()) { mutable.value = s.copy(erro = "Escreva seu relato antes de salvar."); return }
        mutable.value = s.copy(salvando = true, erro = null)
        scope.launch {
            try {
                submit(RegistroDiario(s.humor, s.positivo, s.negativo, ""), account)
                writer?.cancelAndJoin()
                mutable.value = state.value.copy(salvo = true)
                try { store?.save(null) } catch (_: Exception) { mutable.value = state.value.copy(aviso = "Registro salvo. Não foi possível remover a cópia local; descarte-a antes de sair.") }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutable.value = state.value.copy(erro = "Não foi possível confirmar. Consulte o histórico antes de tentar novamente para evitar duplicação.") }
            finally { mutable.value = state.value.copy(salvando = false) }
        }
    }
}
