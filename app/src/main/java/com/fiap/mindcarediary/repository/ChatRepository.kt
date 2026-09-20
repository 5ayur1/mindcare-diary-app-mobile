package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.*

interface ChatRepository {
    suspend fun send(message: String): MiaMessageResponse
    suspend fun save(request: MiaRegistroRequest): RegistroDiario
}

class ApiChatRepository(private val api: ApiService = RetrofitClient.api) : ChatRepository {
    override suspend fun send(message: String) = api.enviarMensagemMia(MiaMessageRequest(message))
    override suspend fun save(request: MiaRegistroRequest) = api.salvarRegistroMia(request)
}
