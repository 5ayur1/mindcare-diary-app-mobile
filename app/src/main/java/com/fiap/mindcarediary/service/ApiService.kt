package com.fiap.mindcarediary.service

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

data class Paciente (
    val nomeUsuario: String,
    val senha: String,
    val nomeCompleto: String,
    val dataNascimento: String,
    val dataHoraAtivacao: String,
    val ativo: Boolean,
    val estadoPaciente: String,
    val profissional: Profissional?,
    val consultas: List<Consulta> = emptyList()

)

data class Profissional (
    val nomeUsuario: String,
    val senha: String,
    val nomeCompleto: String,
    val dataNascimento: String,
    val dataHoraAtivacao: String,
    val ativo: Boolean,
    val tipoProfissional: TipoProfissional,
    val consultas: List<Consulta> = emptyList()
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
    var observacoes: String,
    var recomendacoes: String,
    val registrosDiarios: List<RegistroDiario>,
    val dataHoraCriacao: String,
    val totalPositivos: Int,
    val totalNegativos: Int,
    val resumo: String
)

data class Consulta (
    val profissional: Profissional?,
    val paciente: Paciente?,
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
    @GET("profissionais/{nomeUsuario}/pacientes")
    suspend fun carregarPacientes(@Path("nomeUsuario") nomeUsuario: String): List<Paciente>

    @GET("pacientes/{nomeUsuario}")
    suspend fun retornarDadosPaciente(@Path("nomeUsuario") nomeUsuario: String): Paciente

    @GET("relatoriosSemanais/{nomeUsuario}")
    suspend fun retornarRelatoriosSemanais(@Path("nomeUsuario") nomeUsuario: String): List<RelatorioSemanal>

    @PATCH("relatoriosSemanais/atualizarRelatorioSemanal")
    suspend fun atualizarRelatorioSemanal(@Body relatorioSemanal: RelatorioSemanal)

    @POST("relatoriosSemanais/gerar/{nomeUsuario}")
    suspend fun gerarRelatorioSemanal(@Path("nomeUsuario") nomeUsuario: String): RelatorioSemanal

}