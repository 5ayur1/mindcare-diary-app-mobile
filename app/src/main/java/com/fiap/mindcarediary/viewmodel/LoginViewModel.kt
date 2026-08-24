package com.fiap.mindcarediary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.repository.AuthRepository
import com.fiap.mindcarediary.service.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _dadosResponse = MutableStateFlow<LoginResponse?>(null)

    val dadosResponse: StateFlow<LoginResponse?> = _dadosResponse

    fun efetuarLogin(nomeUsuario: String, senha: String, onSuccess: (LoginResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.efetuarLogin(nomeUsuario, senha)
                if(response.isSuccessful){

                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        _dadosResponse.value = loginResponse
                        repository.saveToken(
                            loginResponse.token
                        )
                        onSuccess(loginResponse)
                    } else {
                        onError(
                            "Resposta de login inválida."
                        )
                    }
                } else {
                    onError(
                        "Usuário ou senha inválidos."
                    )
                }
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _dadosResponse.value = null
                onError(e.message ?: "Erro ao realizar login.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.clearToken()
            } catch (e: Exception) {

            }
        }
    }
}