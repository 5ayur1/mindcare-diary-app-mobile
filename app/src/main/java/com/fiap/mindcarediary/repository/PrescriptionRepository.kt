package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.Prescription
import com.fiap.mindcarediary.service.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class PrescriptionRepository {

    suspend fun salvarPrescricao(
        nomeUsuario: String, profissionalNomeUsuario: RequestBody, issueDate: RequestBody, expirationDate: RequestBody,
        medicines: RequestBody, controlled: RequestBody, arquivo: MultipartBody.Part
    ): Response<Unit> {
        return RetrofitClient.api.salvarPrescricao(nomeUsuario, profissionalNomeUsuario, issueDate,
            expirationDate, medicines, controlled, arquivo)
    }

    suspend fun downloadPrescricaoPdf(
        profissionalNomeUsuario: String, number: String): Response<ResponseBody> {
        return RetrofitClient.api.downloadPrescriptionPdf(profissionalNomeUsuario, number)
    }

    suspend fun retornarReceitasPorPaciente(
        pacienteNomeUsuario: String): Response<List<Prescription>> {
        return RetrofitClient.api.retornarPrescricoes(pacienteNomeUsuario)
    }
}