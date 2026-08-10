package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.service.Prescription
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.service.RelatorioSemanal
import com.fiap.mindcarediary.service.RetrofitClient

class PacienteRepository {

    suspend fun retornarRegistrosDiarios(nomeUsuario: String): List<RegistroDiario> {
        return RetrofitClient.api.retornarRegistrosDiarios(nomeUsuario)
    }

    suspend fun cadastrarRegistroDiario(registroDiario: RegistroDiario, nomeUsuario: String): RegistroDiario {
        return RetrofitClient.api.cadastrarRegistroDiario(nomeUsuario, registroDiario)
    }

    suspend fun retornarDadosPaciente(nomeUsuario: String): Paciente {
        return RetrofitClient.api.retornarDadosPaciente(nomeUsuario)
    }

    suspend fun retornarRelatoriosSemanais(nomeUsuario: String): List<RelatorioSemanal> {
        return RetrofitClient.api.retornarRelatoriosSemanais(nomeUsuario)
    }

    suspend fun retornarPrescricoes(nomeUsuario: String): List<Prescription> {
        return RetrofitClient.api.retornarPrescricoes(nomeUsuario)
    }
}