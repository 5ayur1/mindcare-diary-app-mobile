package com.fiap.mindcarediary.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.profissional.uriToPdfPart
import com.fiap.mindcarediary.repository.PrescriptionRepository
import com.fiap.mindcarediary.service.PdfState
import com.fiap.mindcarediary.service.Prescription
import com.fiap.mindcarediary.service.RegistroDiario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody



class PrescriptionViewModel : ViewModel() {

    private val repository = PrescriptionRepository()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading =
        _isLoading.asStateFlow()

    private val _success =
        MutableStateFlow(false)

    val success =
        _success.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error =
        _error.asStateFlow()

    private val _pdfState = MutableStateFlow<PdfState>(PdfState.Idle)
    val pdfState: StateFlow<PdfState> = _pdfState.asStateFlow()

    private val _receitas = MutableStateFlow<List<Prescription>>(emptyList())

    val receitas: StateFlow<List<Prescription>> = _receitas

    fun enviarPrescricao(
        context: Context,
        nomeUsuario: String,
        profissionalNomeUsuario: String,
        issueDate: String,
        expirationDate: String,
        medicines: List<String>,
        controlled: Boolean,
        pdfUri: Uri
    ) {

        viewModelScope.launch {

            try {

                _isLoading.value = true
                _error.value = null
                _success.value = false

                val pdfPart =
                    uriToPdfPart(
                        context,
                        pdfUri
                    )

                val response =
                    repository
                        .salvarPrescricao(

                            nomeUsuario =
                                nomeUsuario,

                            profissionalNomeUsuario =
                                profissionalNomeUsuario.toRequestBody(),

                            issueDate =
                                issueDate.toRequestBody(),

                            expirationDate =
                                expirationDate.toRequestBody(),

                            medicines =
                                medicines
                                    .joinToString(",").toRequestBody(),

                            controlled =
                                controlled.toString().toRequestBody(),

                            arquivo =
                                pdfPart
                        )

                if (response.isSuccessful) {

                    _success.value = true

                } else {

                    _error.value =
                        "Erro ao enviar receita: HTTP ${response.code()}"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Erro desconhecido"

            } finally {

                _isLoading.value = false
            }
        }
    }


    fun onDownloadPdf(
        receita: Prescription,
        onSuccess: (ByteArray) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                Log.i(
                    "DOWNLOAD_PDF",
                    "profissional=${receita.profissional.nomeUsuario}, " +
                            "number=${receita.number}"
                )

                val response = repository.downloadPrescricaoPdf(
                    profissionalNomeUsuario =
                        receita.profissional.nomeUsuario,
                    number = receita.number
                )

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {
                        onSuccess(body.bytes())
                    } else {
                        onError("PDF não encontrado.")
                    }

                } else {
                    onError(
                        "Erro ao baixar PDF: ${response.code()}"
                    )
                }

            } catch (e: Exception) {
                onError(
                    e.message ?: "Erro ao baixar a receita."
                )
            }
        }
    }

    fun clearPdfState() {
        _pdfState.value = PdfState.Idle
    }

    fun retornarReceitasPorPaciente(pacienteNomeUsuario: String) {
        viewModelScope.launch {
            try {
                val response = repository.retornarReceitasPorPaciente(pacienteNomeUsuario)
                if(response.isSuccessful) {
                    val body = response.body();
                    if (body != null) {
                        _receitas.value = body
                    }
                }
            } catch (e: Exception) {
                Log.i("API_CALL", "Requisição realizada com erro: " + e.message)
                _receitas.value = emptyList()
            }
        }
    }
}