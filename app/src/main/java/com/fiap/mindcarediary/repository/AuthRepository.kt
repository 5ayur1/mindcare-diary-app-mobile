package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.ApiService
import com.fiap.mindcarediary.service.LoginRequest
import com.fiap.mindcarediary.service.LoginResponse
import com.fiap.mindcarediary.service.RetrofitClient
import com.fiap.mindcarediary.service.TokenManager
import retrofit2.Response

class AuthRepository (
    private val tokenManager: TokenManager
) {

    suspend fun efetuarLogin(
        nomeUsuario: String,
        senha: String
    ): Response<LoginResponse> {

        return RetrofitClient.api.efetuarLogin(
            LoginRequest(
                nomeUsuario = nomeUsuario,
                senha = senha
            )
        )
    }

    suspend fun saveToken(token: String) {
        tokenManager.saveToken(token)
    }

    suspend fun clearToken() {
        tokenManager.clearToken()
    }
}