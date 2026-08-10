package com.fiap.mindcarediary.profissional

import PrescriptionViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.viewmodel.PacienteViewModel
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private val Purple = Color(0xFFB86DDE)
private val DarkPurple = Color(0xFF7135DC)
private val BackgroundBlue = Color(0xFFE2F5FC)

private val LightPurple = Color(0xFFF3EEFF)
private val BorderPurple = Color(0xFFC5A2F3)

private val TextDark = Color(0xFF111133)
private val TextGray = Color(0xFF666666)

class NovaPrescricaoProfissionalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pacienteNomeUsuario = intent.getStringExtra("pacienteNomeUsuario") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {

            val prescriptionViewModel: PrescriptionViewModel =
                viewModel()

            NovaPrescricaoProfissionalTela(
                pacienteNomeUsuario = pacienteNomeUsuario,
                email = email,

                onSendPrescriptionClick = {
                        issueDate,
                        expirationDate,
                        medicines,
                        controlled,
                        pdfUri ->

                    prescriptionViewModel.enviarPrescricao(
                        context = this@NovaPrescricaoProfissionalActivity,
                        nomeUsuario = pacienteNomeUsuario,
                        profissionalNomeUsuario = email,
                        issueDate = issueDate,
                        expirationDate = expirationDate,
                        medicines = medicines,
                        controlled = controlled,
                        pdfUri = pdfUri
                    )
                }
            )
        }
    }
}

@Composable
fun TopNovaPrescricaoPaciente(
    abaSelecionada: Int,
    onAbaSelecionada: (Int) -> Unit,
    email: String,
    paciente: Paciente?
) {

    val context = LocalContext.current

    Surface(
        color = Color(0xFFB57BE2),
        shape = RoundedCornerShape(
            bottomStart = 30.dp,
            bottomEnd = 30.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            IconButton(onClick = {
                val intent = Intent(context, InicioProfissionalActivity::class.java)
                intent.putExtra("pacienteNomeUsuario", paciente?.nomeUsuario);
                intent.putExtra("email", email)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "👩",
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {

                    paciente?.nomeCompleto?.let {
                        Text(
                            text = it,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Text(
                        text = "Ficha do Paciente",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAbaSelecionada(0)
                        val intent = Intent(context, DiarioProfissionalActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("pacienteNomeUsuario", paciente?.nomeUsuario)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (abaSelecionada == 0)
                                Color.White
                            else
                                Color(0xFFD5A8EE)
                    )
                ) {

                    Text(
                        text = "Diários",
                        color =
                            if (abaSelecionada == 0)
                                Color(0xFFB04DE6)
                            else
                                Color.White
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAbaSelecionada(1)
                        val intent = Intent(context, RelatorioProfissionalActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("pacienteNomeUsuario", paciente?.nomeUsuario)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (abaSelecionada == 1)
                                Color.White
                            else
                                Color(0xFFD5A8EE)
                    )
                ) {

                    Text(
                        text = "Relatórios",
                        color =
                            if (abaSelecionada == 1)
                                Color(0xFFB04DE6)
                            else
                                Color.White
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAbaSelecionada(2)
                        val intent = Intent(context, PrescricaoProfissionalActivity::class.java)
                        intent.putExtra("pacienteNomeUsuario", paciente?.nomeUsuario);
                        intent.putExtra("email", email)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (abaSelecionada == 2)
                                Color.White
                            else
                                Color(0xFFD5A8EE)
                    )
                ) {

                    Text(
                        text = "Prescrição",
                        color =
                            if (abaSelecionada == 2)
                                Color(0xFFB04DE6)
                            else
                                Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun NovaPrescricaoProfissionalTela(
    pacienteNomeUsuario: String,
    email: String,
    onCancelClick: () -> Unit = {},
    onSendPrescriptionClick: (
        issueDate: String,
        expirationDate: String,
        medicines: List<String>,
        controlled: Boolean,
        pdfUri: Uri
    ) -> Unit = { _, _, _, _, _ -> }
) {

    val pacienteViewModel: PacienteViewModel = viewModel()

    val paciente by pacienteViewModel.paciente.collectAsState()

    var abaSelecionada by remember { mutableStateOf(2) }

    var issueDate by remember {
        mutableStateOf("")
    }

    var expirationDate by remember {
        mutableStateOf("")
    }

    var medicine by remember {
        mutableStateOf("")
    }

    var medicines by remember {
        mutableStateOf(listOf<String>())
    }

    var controlled by remember {
        mutableStateOf(false)
    }

    var selectedPdfUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val canSend =
        issueDate.isNotBlank() &&
                expirationDate.isNotBlank() &&
                medicines.isNotEmpty() &&
                selectedPdfUri != null

    LaunchedEffect(pacienteNomeUsuario) {
        pacienteViewModel.loadDadosPaciente(
            pacienteNomeUsuario
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {

        TopNovaPrescricaoPaciente(
            abaSelecionada = abaSelecionada,
            onAbaSelecionada = {
                abaSelecionada = it
            },
            email,
            paciente
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 18.dp,
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Receitas Médicas",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Button(
                        onClick = onCancelClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8E8E8),
                            contentColor = TextGray
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancelar",
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text(
                            text = "Cancelar",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = DarkPurple,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = "Enviar Nova Receita",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkPurple
                        )

                    }

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Faça o upload do PDF e preencha os dados",
                        fontSize = 12.sp,
                        color = DarkPurple
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    PdfUploader(
                        onPdfSelected = { uri ->
                            selectedPdfUri = uri
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        DateField(
                            label = "Data de Emissão *",
                            value = issueDate,
                            placeholder = "aaaa-mm-dd",
                            onValueChange = {
                                issueDate = it
                            },
                            modifier = Modifier.weight(1f)
                        )

                        DateField(
                            label = "Data de Vencimento *",
                            value = expirationDate,
                            placeholder = "aaaa-mm-dd",
                            onValueChange = {
                                expirationDate = it
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Medicamentos *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    OutlinedTextField(
                        value = medicine,
                        onValueChange = {
                            medicine = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Ex: Escitalopram 10mg",
                                color = Color(0xFFB8B8B8),
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {

                                val value = medicine.trim()

                                if (value.isNotEmpty()) {

                                    medicines =
                                        medicines + value

                                    medicine = ""
                                }
                            }
                            .padding(
                                vertical = 4.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = DarkPurple,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(3.dp)
                        )

                        Text(
                            text = "Adicionar medicamento",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkPurple
                        )
                    }

                    // ----------------------------------------
                    // MEDICINE LIST
                    // ----------------------------------------

                    if (medicines.isNotEmpty()) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        medicines.forEachIndexed { index, item ->

                            MedicineItem(
                                medicine = item,
                                onDelete = {
                                    medicines =
                                        medicines.filterIndexed { i, _ ->
                                            i != index
                                        }
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(LightPurple)
                            .border(
                                width = 2.dp,
                                color = BorderPurple,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                controlled = !controlled
                            }
                            .padding(
                                vertical = 9.dp,
                                horizontal = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = controlled,
                            onCheckedChange = {
                                controlled = it
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF9A9A9A),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = "Receita de medicamento controlado",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Button(
                        onClick = {

                            selectedPdfUri?.let { uri ->

                                onSendPrescriptionClick(
                                    issueDate,
                                    expirationDate,
                                    medicines,
                                    controlled,
                                    uri
                                )
                            }
                        },
                        enabled = canSend,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(295.dp)
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB57BE2),
                            disabledContainerColor = Color(0xFFD4C1F3),
                            contentColor = Color.White,
                            disabledContentColor = Color.White
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Enviar Receita ao Paciente",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun uriToPdfPart(
    context: Context,
    uri: Uri
): MultipartBody.Part {

    val fileName = getFileName(
        context,
        uri
    )

    val inputStream =
        context.contentResolver
            .openInputStream(uri)
            ?: throw IOException(
                "Não foi possível abrir o PDF"
            )

    val bytes =
        inputStream.use {
            it.readBytes()
        }

    val requestBody =
        bytes.toRequestBody(
            "application/pdf".toMediaType()
        )

    return MultipartBody.Part.createFormData(
        "arquivo",
        fileName,
        requestBody
    )
}

fun getFileName(
    context: Context,
    uri: Uri
): String {

    var fileName: String? = null

    if (uri.scheme == "content") {

        context.contentResolver
            .query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )
            ?.use { cursor ->

                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(0)
                }
            }
    }

    return fileName
        ?: uri.path?.substringAfterLast('/')
        ?: "arquivo.pdf"
}

@Composable
fun PdfUploader(
    onPdfSelected: (Uri) -> Unit
) {
    val context = LocalContext.current

    var selectedFileName by remember {
        mutableStateOf<String?>(null)
    }

    val pdfPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            uri ?: return@rememberLauncherForActivityResult

            val mimeType =
                context.contentResolver.getType(uri)

            if (mimeType != "application/pdf") {
                return@rememberLauncherForActivityResult
            }

            selectedFileName =
                getFileName(context, uri)

            onPdfSelected(uri)
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(158.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8F8F8))
            .border(
                width = 2.dp,
                color = Color(0xFFD7D7D7),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable {
                pdfPicker.launch(
                    arrayOf("application/pdf")
                )
            }
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFFE4D9FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = "Upload PDF",
                tint = DarkPurple,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = selectedFileName
                ?: "Selecionar PDF",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = if (selectedFileName == null)
                "Clique para selecionar o arquivo"
            else
                "PDF selecionado",

            fontSize = 12.sp,
            color = Color(0xFFB5B5B5)
        )
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 13.sp,
                    color = Color(0xFFBDBDBD)
                )
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun MedicineItem(
    medicine: String,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 3.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = DarkPurple,
            modifier = Modifier.size(17.dp)
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Text(
            text = medicine,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = TextDark
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(30.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remover medicamento",
                tint = Color.Gray,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}