package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.Consulta
import com.fiap.mindcarediary.service.RecomendacaoHorario
import com.fiap.mindcarediary.service.RetrofitClient

class AgendamentoRepository {

    suspend fun salvarAgendamento(consulta: Consulta) {
        return RetrofitClient.api.salvarAgendamento(consulta)
    }

    suspend fun recomendarHorarios(tipoProfissional: String): List<RecomendacaoHorario> {
        return RetrofitClient.api.recomendarHorarios(tipoProfissional)
    }

    suspend fun recomendarHorariosParaDataInformada(dataInformada: String, nomeUsuario: String): List<RecomendacaoHorario> {
        return RetrofitClient.api.recomendarHorariosParaDataInformada(dataInformada, nomeUsuario)
    }
}