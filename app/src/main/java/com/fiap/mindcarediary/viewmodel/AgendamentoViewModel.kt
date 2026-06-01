package com.fiap.mindcarediary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.AgendamentoRepository
import com.fiap.mindcarediary.service.Consulta
import com.fiap.mindcarediary.service.RecomendacaoHorario
import com.fiap.mindcarediary.service.TipoProfissional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgendamentoViewModel : ViewModel() {

    private val repository = AgendamentoRepository()

    private val _horarios = MutableStateFlow<List<RecomendacaoHorario>>(emptyList())

    val horarios: StateFlow<List<RecomendacaoHorario>> = _horarios

    private val _tipoProfissional =
        MutableStateFlow<TipoProfissional?>(null)

    val tipoProfissional: StateFlow<TipoProfissional?> =
        _tipoProfissional

    fun selecionarTipo(tipo: TipoProfissional) {
        _tipoProfissional.value = tipo
    }

    fun cadastrarAgendamento(consulta: Consulta) {
        viewModelScope.launch {
            try {
                repository.salvarAgendamento(consulta)
            } catch (e: Exception) {

            }
        }
    }

    fun carregarHorarios(tipoProfissional: String) {
        viewModelScope.launch {
            try {
                _horarios.value = repository.recomendarHorarios(tipoProfissional)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _horarios.value = emptyList()
            }
        }
    }

    fun carregarHorariosParaDataInformada(
        nomeUsuario: String,
        dataInformada: String
    ) {
        viewModelScope.launch {
            try {
                _horarios.value =
                    repository.recomendarHorariosParaDataInformada(dataInformada, nomeUsuario)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _horarios.value = emptyList()
            }
        }
    }

}