package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.service.RetrofitClient

class PacienteRepository {

    suspend fun retornarRegistrosDiarios(nomeUsuario: String): List<RegistroDiario> {
        return RetrofitClient.api.retornarRegistrosDiarios(nomeUsuario)
    }

    suspend fun cadastrarRegistroDiario(registroDiario: RegistroDiario, nomeUsuario: String): RegistroDiario {
        return RetrofitClient.api.cadastrarRegistroDiario(nomeUsuario, registroDiario)
    }
}