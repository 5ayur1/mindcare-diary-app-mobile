package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class PrescriptionRepository {

    suspend fun salvarPrescricao(
        nomeUsuario: String, profissionalNomeUsuario: RequestBody, issueDate: RequestBody, expirationDate: RequestBody,
        medicines: RequestBody, controlled: RequestBody, arquivo: MultipartBody.Part
    ): Response<Unit> {
        return RetrofitClient.api.salvarPrescricao(nomeUsuario, profissionalNomeUsuario, issueDate,
            expirationDate, medicines, controlled, arquivo)
    }
}