package com.fiap.mindcarediary.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class Paciente (
    val id: Int,
    val nomeUsuario: String,
    val senha: String,
    val nomeCompleto: String,
    val dataNascimento: String,
    val ativo: Boolean,
    val estadoPaciente: String
)

data class Profissional (
    val id: Int,
    val nomeUsuario: String,
    val senha: String,
    val nomeCompleto: String,
    val dataNascimento: String
)

interface ApiService {

    @GET("/pacientes/{idPaciente}")
    suspend fun getPaciente(idPaciente: Int): Paciente

    @POST("/pacientes")
    suspend fun salvarCadastroPaciente(@Body request: Paciente): Response<Paciente>

}