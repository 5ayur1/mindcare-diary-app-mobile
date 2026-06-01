package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.Profissional
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.service.RetrofitClient

class ProfissionalRepository {

    suspend fun retornarDadosProfissional(nomeUsuario: String): Profissional {
        return RetrofitClient.api.retornarDadosProfissional(nomeUsuario)
    }

    suspend fun buscarProfissionaisProTipo(tipoProfissional: String): List<Profissional> {
        return RetrofitClient.api.buscarProfissionaisProTipo(tipoProfissional)
    }
}