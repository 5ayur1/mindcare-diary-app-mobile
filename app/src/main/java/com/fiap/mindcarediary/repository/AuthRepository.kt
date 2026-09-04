package com.fiap.mindcarediary.repository

import android.util.Log
import com.fiap.mindcarediary.service.ApiService
import com.fiap.mindcarediary.service.LoginRequest
import com.fiap.mindcarediary.service.LoginResponse
import com.fiap.mindcarediary.service.RetrofitClient
import com.fiap.mindcarediary.service.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class AuthRepository (
    private val tokenManager: TokenManager,
) {
    private val firebaseMessaging =
        FirebaseMessaging.getInstance()

    suspend fun getFirebaseToken(nomeUsuario: String) {
        try {
            val token = firebaseMessaging.token.await()
            RetrofitClient.api.salvarToken(nomeUsuario, token)
            Log.d(
                "FCM",
                "TOKEN = $token"
            )
        } catch (e: Exception) {
            println(
                "Erro ao obter token FCM: ${e.message}"
            )
        }
    }

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