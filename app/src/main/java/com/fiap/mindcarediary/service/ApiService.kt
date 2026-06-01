package com.fiap.mindcarediary.service

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

data class Paciente (
    val nomeUsuario: String,
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
    val dataNascimento: String,
    val tipoProfissional: TipoProfissional
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
    val dataHoraCriacao: String,
    val totalPositivos: Int,
    val totalNegativos: Int,
)

data class Consulta (
    val profissional: Profissional?,
    val atendida: Boolean,
    val cancelada: Boolean,
    val dataHoraConsulta: String?
)
data class RecomendacaoHorario (
    val score: Int,
    val especialidade: String,
    val dataHoraConsulta: String
)

enum class TipoProfissional {
    PSICOLOGO,
    PSIQUIATRA
}


interface ApiService {

    @GET("registrosDiarios/{nomeUsuario}")
    suspend fun retornarRegistrosDiarios(@Path("nomeUsuario") nomeUsuario: String): List<RegistroDiario>

    @POST("registrosDiarios/cadastrarRegistroDiario/{nomeUsuario}")
    suspend fun cadastrarRegistroDiario(@Path("nomeUsuario") nomeUsuario: String, @Body request: RegistroDiario): RegistroDiario

    @POST("agendamentos")
    suspend fun salvarAgendamento(@Body consulta: Consulta)

    @GET("agendamentos/recomendarHorarios")
    suspend fun recomendarHorarios(@Query("tipoProfissional") tipoProfissional: String): List<RecomendacaoHorario>

    @GET("agendamentos/recomendarHorarios/{dataInformada}/profissional/{nomeUsuario}")
    suspend fun recomendarHorariosParaDataInformada(@Path("dataInformada") dataInformada: String, @Path("nomeUsuario") nomeUsuario: String): List<RecomendacaoHorario>

    @GET("profissionais/{nomeUsuario}")
    suspend fun retornarDadosProfissional(@Path("nomeUsuario") nomeUsuario: String): Profissional

    @GET("profissionais/tipoProfissional/{tipoProfissional}")
    suspend fun buscarProfissionaisProTipo(@Path("tipoProfissional") tipoProfissional: String): List<Profissional>

    @GET("relatoriosSemanais/{nomeUsuario}")
    suspend fun carregarRelatoriosSemanais(@Path("nomeUsuario") nomeUsuario: String): List<RelatorioSemanal>



}