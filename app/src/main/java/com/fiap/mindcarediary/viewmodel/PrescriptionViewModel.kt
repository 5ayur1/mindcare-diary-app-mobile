import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.mindcarediary.profissional.uriToPdfPart
import com.fiap.mindcarediary.repository.PrescriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
}