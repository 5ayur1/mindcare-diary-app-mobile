package com.fiap.mindcarediary.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class Paciente (val nomeUsuario: String,
    val senha: String,
    val nomeCompleto: String,
    val dataNascimento: String,
    val ativo: Boolean,
    val estadoPaciente: String
)

data class Profissional (
    val nomeUsuario: String,
    val senha: String,
    val nomeCompleto: String,
    val dataNascimento: String
)

data class RegistroDiario (
    val nivelHumor: String,
    val pontosPositivos: String,
    val dificuldadesDesafios: String,
    val dataHoraCriacao: String
)

data class RelatorioSemanal (
    val paciente: Paciente,
    val faixaDeDatas: String,
    val relatorioIA: String,
    val observacoes: String,
    val recomendacoes: String,
    val registrosDiarios: List<RegistroDiario>,
    val dataHoraCriacao: String
)

interface ApiService {

    @GET("registrosDiarios/{nomeUsuario}")
    suspend fun retornarRegistrosDiarios(@Path("nomeUsuario") nomeUsuario: String): List<RegistroDiario>

    @POST("registrosDiarios/cadastrarRegistroDiario/{nomeUsuario}")
    suspend fun cadastrarRegistroDiario(@Path("nomeUsuario") nomeUsuario: String, @Body request: RegistroDiario): RegistroDiario

}