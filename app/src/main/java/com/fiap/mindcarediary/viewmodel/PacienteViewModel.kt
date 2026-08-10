package com.fiap.mindcarediary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.PacienteRepository
import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.service.Prescription
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.service.RelatorioSemanal
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PacienteViewModel : ViewModel() {

    private val repository = PacienteRepository()

    private val _registrosDiarios = MutableStateFlow<List<RegistroDiario>>(emptyList())
    val registrosDiarios: StateFlow<List<RegistroDiario>> = _registrosDiarios

    private val _paciente = MutableStateFlow<Paciente?>(null)

    val paciente: StateFlow<Paciente?> = _paciente

    private val _relatorios = MutableStateFlow<List<RelatorioSemanal>>(emptyList())

    val relatorios: StateFlow<List<RelatorioSemanal>> = _relatorios

    private val _prescriptions = MutableStateFlow<List<Prescription>>(emptyList())

    val prescriptions: StateFlow<List<Prescription>> = _prescriptions

    fun loadRegistrosDiarios(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                _registrosDiarios.value = repository.retornarRegistrosDiarios(nomeUsuario)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _registrosDiarios.value = emptyList()
            }
        }
    }

    fun cadastrarRegistroDiario(registroDiario: RegistroDiario, nomeUsuario: String) {
        viewModelScope.launch {
            try {
                repository.cadastrarRegistroDiario(registroDiario, nomeUsuario)
            } catch (e: Exception) {

            }
        }
    }

    fun loadDadosPaciente(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                val retorno = repository.retornarDadosPaciente(nomeUsuario)
                Log.i("PACIENTE", "Paciente retornado: $retorno")
                _paciente.value = retorno
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _paciente.value = null
            }
        }
    }

    fun loadRelatoriosSemanais(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                val retorno = repository.retornarRelatoriosSemanais(nomeUsuario)
                Log.i("RELATORIOS", "Relatórios retornados: $retorno")
                _relatorios.value = retorno
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _relatorios.value = emptyList()
            }
        }
    }

    fun loadPrescriptions(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                val retorno = repository.retornarPrescricoes(nomeUsuario)
                Log.i("PRESCRIPTIONS", "Prescrições retornadas: $retorno")
                _prescriptions.value = retorno
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _prescriptions.value = emptyList()
            }
        }
    }
}