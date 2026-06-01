package com.fiap.mindcarediary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.PacienteRepository
import com.fiap.mindcarediary.repository.ProfissionalRepository
import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.service.Profissional
import com.fiap.mindcarediary.service.RegistroDiario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfissionalViewModel : ViewModel() {

    private val repository = ProfissionalRepository()

    private val _dadosProfissional = MutableStateFlow<Profissional?>(null)

    val dadosProfissional: StateFlow<Profissional?> = _dadosProfissional

    private val _profissionais = MutableStateFlow<List<Profissional>>(emptyList())

    val profissionais: StateFlow<List<Profissional>> = _profissionais

    private val _pacientes = MutableStateFlow<List<Paciente>>(emptyList())

    val pacientes: StateFlow<List<Paciente>> = _pacientes

    fun loadDadosProfissional(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                _dadosProfissional.value = repository.retornarDadosProfissional(nomeUsuario)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _dadosProfissional.value = null
            }
        }
    }

    fun buscarProfissionaisProTipo(tipoProfissional: String) {
        viewModelScope.launch {
            try {
                _profissionais.value = repository.buscarProfissionaisProTipo(tipoProfissional)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _profissionais.value = emptyList()
            }
        }
    }

    fun loadPacientes(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                _pacientes.value = repository.carregarPacientes(nomeUsuario)
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _pacientes.value = emptyList()
            }
        }
    }
}