package com.fiap.mindcarediary.repository

import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.service.RetrofitClient

class PacienteRepository {

    suspend fun getPaciente(idPaciente: Int): Paciente {
        return RetrofitClient.api.getPaciente(idPaciente)
    }
}