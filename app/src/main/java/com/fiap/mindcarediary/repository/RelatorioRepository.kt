package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.Profissional
import com.fiap.mindcarediary.service.RelatorioSemanal
import com.fiap.mindcarediary.service.RetrofitClient

class RelatorioRepository {

    suspend fun carregarRelatoriosSemanais(nomeUsuario: String): List<RelatorioSemanal> {
        return RetrofitClient.api.carregarRelatoriosSemanais(nomeUsuario)
    }

    suspend fun atualizarRelatorioSemanal(relatorioSemanal: RelatorioSemanal) {
        return RetrofitClient.api.atualizarRelatorioSemanal(relatorioSemanal)
    }

    suspend fun gerarRelatorioSemanal(nomeUsuario: String): RelatorioSemanal {
        return RetrofitClient.api.gerarRelatorioSemanal(nomeUsuario)
    }

}