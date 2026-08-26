package com.fiap.mindcarediary.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
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
    val consultas: List<Consulta> = emptyList(),
    val registroProfissional: String
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
    val resumo: String,
    val number: String
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

data class Prescription(
    val number: String,
    val issueDate: String,
    val expirationDate: String,
    val daysRemaining: Int,
    val profissional: Profissional,
    val medicines: List<String>,
    val controlled: Boolean,
    val valid: Boolean
)

data class PrescriptionDcoument(
    val prescription: Prescription,
    val issueDate: String,
    val expirationDate: String,
    val daysRemaining: String,
    val doctorInfo: Profissional,
    val medicines: List<String>,
    val controlled: Boolean,
    val valid: Boolean
)

enum class TipoProfissional {
    PSICOLOGO,
    PSIQUIATRA
}

sealed class PdfState {

    data object Idle : PdfState()

    data object Loading : PdfState()

    data class Success(
        val pdfBytes: MultipartBody.Part
    ) : PdfState()

    data class Error(
        val message: String
    ) : PdfState()
}

data class LoginRequest (
    val nomeUsuario: String,
    val senha: String
)

data class LoginResponse(
    val token: String,
    val userRole: String
)

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

    @Multipart
    @POST("prescriptions/{pacienteNomeUsuario}")
    suspend fun salvarPrescricao(@Path("pacienteNomeUsuario") pacienteNomeUsuario: String, @Part("issueDate") issueDate: RequestBody, @Part("expirationDate") expirationDate: RequestBody,
                                 @Part("medicines") medicines: RequestBody, @Part("controlled") controlled: RequestBody, @Part arquivo: MultipartBody.Part): Response<ResponseBody>

    @POST("prescriptions/{profissionalNomeUsuario}/{number}/pdf")
    suspend fun downloadPrescriptionPdf(
        @Path("profissionalNomeUsuario") profissionalNomeUsuario: String, @Path("number") number: String): Response<ResponseBody>

    @GET("pacientes/{nomeUsuario}/prescriptions")
    suspend fun retornarPrescricoes(@Path("nomeUsuario") nomeUsuario: String): Response<List<Prescription>>

    @POST("login")
    suspend fun efetuarLogin(@Body dados: LoginRequest): Response<LoginResponse>
}