package com.fiap.mindcarediary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.AgendamentoRepository
import com.fiap.mindcarediary.repository.RelatorioRepository
import com.fiap.mindcarediary.service.RecomendacaoHorario
import com.fiap.mindcarediary.service.RelatorioSemanal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RelatorioViewModel : ViewModel() {

    private val repository = RelatorioRepository()

    private val _relatorios = MutableStateFlow<List<RelatorioSemanal>>(emptyList())

    val relatorios: StateFlow<List<RelatorioSemanal>> = _relatorios

    private val _relatorio = MutableStateFlow<RelatorioSemanal?>(null)

    val relatorio: StateFlow<RelatorioSemanal?> = _relatorio

    fun carregarRelatoriosSemanais(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                _relatorios.value = repository.carregarRelatoriosSemanais(nomeUsuario)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _relatorios.value = emptyList()
            }
        }
    }

    fun atualizarRelatorioSemanal(relatorioSemanal: RelatorioSemanal) {
        viewModelScope.launch {
            try {
                repository.atualizarRelatorioSemanal(relatorioSemanal)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
            }
        }
    }

    fun gerarRelatorioSemanal(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                _relatorio.value = repository.gerarRelatorioSemanal(nomeUsuario)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _relatorio.value = null
            }
        }
    }
}