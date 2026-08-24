package com.fiap.mindcarediary.profissional

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.fiap.mindcarediary.service.Prescription
import com.fiap.mindcarediary.viewmodel.PacienteViewModel

private val Purple = Color(0xFFB86DDE)
private val DarkPurple = Color(0xFF7B2CBF)
private val LightPurple = Color(0xFFF0E8FF)

private val BackgroundBlue = Color(0xFFE3F5FC)

private val Green = Color(0xFF18A957)
private val LightGreen = Color(0xFFE9FAF1)

private val Red = Color(0xFFD71920)
private val LightRed = Color(0xFFFFE9EA)

private val Gray = Color(0xFFF3F3F3)
private val BorderGray = Color(0xFFD7D7D7)

private val TextDark = Color(0xFF111133)
private val TextGray = Color(0xFF777777)

class PrescricaoProfissionalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pacienteNomeUsuario = intent.getStringExtra("pacienteNomeUsuario") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            PrescricaoProfissionalTela(pacienteNomeUsuario, email)
        }
    }
}

@Composable
fun PrescricaoProfissionalTela(
    pacienteNomeUsuario: String,
    email: String,
    onPdfClick: (Prescription) -> Unit = {},
    onDeleteClick: (Prescription) -> Unit = {}
) {

    val pacienteViewModel: PacienteViewModel = viewModel()

    val paciente by pacienteViewModel.paciente.collectAsState()

    val prescriptions by pacienteViewModel.prescriptions.collectAsState()

    var abaSelecionada by remember { mutableStateOf(2) }

    LaunchedEffect(pacienteNomeUsuario) {
        if (pacienteNomeUsuario != null) {
            pacienteViewModel.loadDadosPaciente(pacienteNomeUsuario)
        }
    }

    LaunchedEffect(pacienteNomeUsuario) {
        if (pacienteNomeUsuario != null) {
            pacienteViewModel.loadPrescriptions(pacienteNomeUsuario)
        }
    }

    val context = LocalContext.current

    Scaffold() { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFDDEEF7))
        ) {

            TopPrescricaoPaciente(
                abaSelecionada = abaSelecionada,
                onAbaSelecionada = {
                    abaSelecionada = it
                },
                email,
                paciente
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 18.dp,
                    bottom = 30.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                item {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = TextDark,
                                modifier = Modifier.size(26.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Receitas Médicas",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Button(
                            onClick = {
                                val intent = Intent(context, NovaPrescricaoProfissionalActivity::class.java)
                                intent.putExtra("pacienteNomeUsuario", paciente?.nomeUsuario);
                                intent.putExtra("email", email)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA937D3)
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "Nova receita",
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                items(prescriptions) { prescription ->

                    PrescriptionCard(
                        prescription = prescription,
                        onPdfClick = {
                            onPdfClick(prescription)
                        },
                        onDeleteClick = {
                            onDeleteClick(prescription)
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun PrescriptionCard(
    prescription: Prescription,
    onPdfClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            PrescriptionTitle(
                prescription = prescription
            )

            Spacer(modifier = Modifier.height(12.dp))

            DateInformation(
                prescription = prescription
            )

            Spacer(modifier = Modifier.height(12.dp))

            DoctorInformation(
                prescription = prescription
            )

            Spacer(modifier = Modifier.height(12.dp))

            MedicinesInformation(
                prescription = prescription
            )

            Spacer(modifier = Modifier.height(10.dp))

            PrescriptionActions(
                prescription = prescription,
                onPdfClick = onPdfClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}


@Composable
fun TopPrescricaoPaciente(
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
private fun PrescriptionTitle(
    prescription: Prescription
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Receita #${prescription.number}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7135DC)
            )

            if (prescription.controlled) {

                Spacer(modifier = Modifier.width(8.dp))

                StatusChip(
                    text = "Controlado",
                    background = LightPurple,
                    textColor = Color(0xFF7135DC),
                    dotColor = null
                )
            }
        }

        StatusChip(
            text = if (prescription.valid) "Válida" else "Vencida",
            background = if (prescription.valid)
                LightGreen
            else
                LightRed,
            textColor = if (prescription.valid)
                Green
            else
                Red,
            dotColor = if (prescription.valid)
                Green
            else
                Red
        )
    }
}


// ---------------------------------------------------------
// STATUS CHIP
// ---------------------------------------------------------

@Composable
private fun StatusChip(
    text: String,
    background: Color,
    textColor: Color,
    dotColor: Color?
) {

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .padding(
                horizontal = 11.dp,
                vertical = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (dotColor != null) {

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )

            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}


// ---------------------------------------------------------
// DATE INFORMATION
// ---------------------------------------------------------

@Composable
private fun DateInformation(
    prescription: Prescription
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        InfoBox(
            modifier = Modifier.weight(1f),
            title = "Emissão",
            value = prescription.issueDate,
            subtitle = "",
            background = Color(0xFFF5F5F5),
            borderColor = BorderGray,
            titleColor = TextGray,
            valueColor = TextDark
        )

        InfoBox(
            modifier = Modifier.weight(1f),
            title = "Vencimento",
            value = prescription.expirationDate,
            subtitle = prescription.daysRemaining.toString() + " dias restantes",
            background = if (prescription.valid)
                LightGreen
            else
                LightRed,
            borderColor = if (prescription.valid)
                Color(0xFFB8F0D2)
            else
                Color(0xFFFFA9AE),
            titleColor = if (prescription.valid)
                Color(0xFF16833F)
            else
                Red,
            valueColor = if (prescription.valid)
                Color(0xFF16833F)
            else
                Red
        )
    }
}


@Composable
private fun InfoBox(
    modifier: Modifier,
    title: String,
    value: String,
    subtitle: String?,
    background: Color,
    borderColor: Color,
    titleColor: Color,
    valueColor: Color
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(background)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(13.dp)
            )
            .padding(11.dp)
    ) {

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )

        if (subtitle != null) {

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF999999)
            )
        }
    }
}


// ---------------------------------------------------------
// DOCTOR
// ---------------------------------------------------------

@Composable
private fun DoctorInformation(
    prescription: Prescription
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFFF5F5F5))
            .border(
                2.dp,
                BorderGray,
                RoundedCornerShape(13.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {

            Text(
                text = prescription.profissional.nomeCompleto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = prescription.profissional.tipoProfissional.toString(),
                fontSize = 12.sp,
                color = TextDark
            )
        }
    }
}


// ---------------------------------------------------------
// MEDICINES
// ---------------------------------------------------------

@Composable
private fun MedicinesInformation(
    prescription: Prescription
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFFF5F2FF))
            .border(
                width = 2.dp,
                color = Color(0xFFC7A5F4),
                shape = RoundedCornerShape(13.dp)
            )
            .padding(12.dp)
    ) {

        Text(
            text = "${prescription.medicines.size} MEDICAMENTOS PRESCRITOS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7135DC)
        )

        Spacer(modifier = Modifier.height(7.dp))

        prescription.medicines.forEach { medicine ->

            Row(
                modifier = Modifier.padding(
                    vertical = 1.dp
                )
            ) {

                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = medicine,
                    fontSize = 12.sp,
                    color = TextDark
                )
            }
        }
    }
}


// ---------------------------------------------------------
// ACTIONS
// ---------------------------------------------------------

@Composable
private fun PrescriptionActions(
    prescription: Prescription,
    onPdfClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = "Enviada em ${prescription.issueDate}",
            fontSize = 11.sp,
            color = Color.LightGray,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onPdfClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEAEAEA)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp
            )
        ) {

            Text(
                text = "📄",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "PDF",
                color = Color(0xFF555555),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFFFFE6E6))
        ) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Excluir receita",
                tint = Color(0xFFFF6B6B),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
