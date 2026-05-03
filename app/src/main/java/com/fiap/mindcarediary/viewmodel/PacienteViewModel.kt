package com.fiap.mindcarediary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.PacienteRepository
import com.fiap.mindcarediary.service.RegistroDiario
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PacienteViewModel : ViewModel() {

    private val repository = PacienteRepository()

    private val _registrosDiarios = MutableStateFlow<List<RegistroDiario>>(emptyList())
    val registrosDiarios: StateFlow<List<RegistroDiario>> = _registrosDiarios

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
}