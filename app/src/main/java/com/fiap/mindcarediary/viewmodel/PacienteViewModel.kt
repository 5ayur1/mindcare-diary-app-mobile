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
import kotlinx.coroutines.CancellationException

class PacienteViewModel : ViewModel() {

    private val repository = PacienteRepository()

    private val _salvandoDiario = MutableStateFlow(false)
    val salvandoDiario: StateFlow<Boolean> = _salvandoDiario
    private val _diarioSalvo = MutableStateFlow(false)
    val diarioSalvo: StateFlow<Boolean> = _diarioSalvo
    private val _erroDiario = MutableStateFlow<String?>(null)
    val erroDiario: StateFlow<String?> = _erroDiario

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
                _registrosDiarios.value = emptyList()
            }
        }
    }

    fun cadastrarRegistroDiario(registroDiario: RegistroDiario, nomeUsuario: String) {
        if (_salvandoDiario.value || _diarioSalvo.value) return
        _salvandoDiario.value = true
        _erroDiario.value = null
        viewModelScope.launch {
            try {
                repository.cadastrarRegistroDiario(registroDiario, nomeUsuario)
                _diarioSalvo.value = true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: Exception) {
                _erroDiario.value = "Não foi possível confirmar o salvamento. Consulte o histórico antes de tentar novamente."
            } finally {
                _salvandoDiario.value = false
            }
        }
    }

    fun loadDadosPaciente(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                val retorno = repository.retornarDadosPaciente(nomeUsuario)
                _paciente.value = retorno
            } catch (e: Exception) {
                _paciente.value = null
            }
        }
    }

    fun loadRelatoriosSemanais(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                val retorno = repository.retornarRelatoriosSemanais(nomeUsuario)
                _relatorios.value = retorno
            } catch (e: Exception) {
                _relatorios.value = emptyList()
            }
        }
    }

    fun loadPrescriptions(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                val retorno = repository.retornarPrescricoes(nomeUsuario)
                if(retorno.isSuccessful) {
                    val body = retorno.body()
                    if(body != null) {
                        _prescriptions.value = body
                    }
                }
            } catch (e: Exception) {
                _prescriptions.value = emptyList()
            }
        }
    }
}
