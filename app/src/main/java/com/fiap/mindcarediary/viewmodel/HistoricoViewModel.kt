package com.fiap.mindcarediary.viewmodel

import com.fiap.mindcarediary.model.*
import com.fiap.mindcarediary.repository.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.service.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HistoricoState(val registros: List<RegistroDiario> = emptyList(), val carregando: Boolean = false,
    val erro: String? = null, val temMais: Boolean = false, val pagina: Int = -1)

class HistoricoViewModel(private val repository: HistoricoRepository = ApiHistoricoRepository(), private val testScope: CoroutineScope? = null) : ViewModel() {
    private val mutable = MutableStateFlow(HistoricoState())
    val state = mutable.asStateFlow()
    private var filtro = HistoricoFiltro()
    fun buscar(novo: HistoricoFiltro = filtro) {
        if (state.value.carregando) return
        try {
            val inicio = novo.inicio.takeIf { it.isNotBlank() }?.let(LocalDate::parse)
            val fim = novo.fim.takeIf { it.isNotBlank() }?.let(LocalDate::parse)
            require(inicio == null || fim == null || !inicio.isAfter(fim))
            require(novo.texto.length <= 200)
        } catch (_: Exception) { mutable.value = state.value.copy(erro = "Confira as datas (AAAA-MM-DD) e o período informado."); return }
        filtro = novo
        mutable.value = HistoricoState()
        carregar(0)
    }
    fun mais() { if (state.value.temMais && !state.value.carregando) carregar(state.value.pagina + 1) }
    fun tentarNovamente() { if (!state.value.carregando) carregar(state.value.pagina + 1) }
    private fun carregar(pagina: Int) {
        mutable.value = state.value.copy(carregando = true, erro = null)
        (testScope ?: viewModelScope).launch {
            try {
                val result = repository.buscar(filtro, pagina)
                mutable.value = state.value.copy(registros = (state.value.registros + result.registros).distinctBy { it.id }, pagina = pagina, temMais = result.temMais)
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutable.value = state.value.copy(erro = "Não foi possível carregar o histórico. Confira sua conexão e tente novamente.") }
            finally { mutable.value = state.value.copy(carregando = false) }
        }
    }
}
