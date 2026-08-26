package com.fiap.mindcarediary.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.BemVindoActivity
import com.fiap.mindcarediary.service.Prescription
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.repository.AuthRepository
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import com.fiap.mindcarediary.viewmodel.PrescriptionViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import java.io.File
import java.time.LocalDate

private val MindCareBlue = Color(0xFF65B9ED)
private val MindCareLightBlue = Color(0xFFE4F5FD)
private val MindCarePurple = Color(0xFF6335D8)
private val MindCarePink = Color(0xFFEF3F9A)

private val ValidGreen = Color(0xFF0B7D3B)
private val ValidGreenBackground = Color(0xFFE0F7EB)

private val ExpiredRed = Color(0xFFD20D0D)
private val ExpiredBackground = Color(0xFFFFE6E6)

class MinhasPrescricoesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            MinhasReceitasTela(email)
        }
    }
}

@Composable
fun MinhasReceitasTela(
    email: String
) {

    val prescriptionViewModel: PrescriptionViewModel = viewModel()
    val receitas by prescriptionViewModel.receitas.collectAsState();

    var context = LocalContext.current

    val tokenManager = remember {
        TokenManager(context.applicationContext)
    }

    val repository = remember {
        AuthRepository(
            tokenManager = tokenManager
        )
    }

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(repository)
    )

    LaunchedEffect(email) {
        if (email.isNotBlank()) {
            prescriptionViewModel.retornarReceitasPorPaciente(email)
        }
    }

    Scaffold(
        containerColor = MindCareLightBlue,
        bottomBar = {
            BottomPrescriptionBar(email)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF6FC))
        ) {

            Header(loginViewModel)

            Spacer(modifier = Modifier.height(18.dp))

            PrescriptionSummary(receitas)

            PrescriptionWarning()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(receitas) { receita ->

                    PrescriptionCard(
                        receita = receita,
                        onDownloadPdf = {

                            prescriptionViewModel.onDownloadPdf(
                                receita = receita,

                                onSuccess = { pdfBytes ->
                                    val file = File(
                                        context.cacheDir,
                                        "receita_${receita.number}.pdf"
                                    )

                                    file.writeBytes(pdfBytes)

                                    val intent = Intent(
                                        context,
                                        PdfViewerActivity::class.java
                                    ).apply {
                                        putExtra(
                                            "pdfPath",
                                            file.absolutePath
                                        )
                                    }

                                    context.startActivity(intent)
                                },

                                onError = { mensagem ->
                                    Toast.makeText(
                                        context,
                                        mensagem,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
fun BottomPrescriptionBar(
    email: String
) {
    NavigationBar(
        containerColor = Color(0xFFD8E4FF),
    ) {

        val context = LocalContext.current

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, InicioPacienteActivity::class.java)
                intent.putExtra("email", email);
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, DiarioPacienteActivity::class.java)
                intent.putExtra("email", email)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    Icons.Default.Book,
                    contentDescription = "Diário"
                )
            },
            label = { Text("Diário") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, RelatorioActivity::class.java)
                intent.putExtra("email", email)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    Icons.Default.Description,
                    contentDescription = "Relatório"
                )
            },
            label = { Text("Relatório") }
        )

        NavigationBarItem(
            selected = true,
            onClick = {
                val intent = Intent(context, MinhasPrescricoesActivity::class.java)
                intent.putExtra("email", email)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    Icons.Default.Healing,
                    contentDescription = "Prescrição"
                )
            },
            label = { Text("Prescrição") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF3D9B),
                selectedTextColor = Color(0xFFFF3D9B),
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun Header(
    loginViewModel: LoginViewModel
) {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp
                )
            )
            .background(MindCareBlue)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp)
                )

                IconButton(onClick = {
                    loginViewModel.logout()
                    val intent = Intent(context, BemVindoActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null
                    )
                    }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Spacer(
                    modifier = Modifier.width(18.dp)
                )

                Text(
                    text = "Minhas Receitas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A0A54)
                )
            }
        }
    }
}

@Composable
private fun PrescriptionSummary(
    receitas: List<Prescription>
) {

    val hoje = LocalDate.now()

    val receitasAtivas = receitas.filter { receita ->
        try {
            val emissao = LocalDate.parse(receita.issueDate)
            val vencimento = LocalDate.parse(receita.expirationDate)

            receita.valid &&
                    !hoje.isBefore(emissao) &&
                    !hoje.isAfter(vencimento)

        } catch (e: Exception) {
            false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 30.dp,
                vertical = 25.dp
            )
            .height(115.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF86C9F2)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFB1DDF7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Receitas ativas",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Text(
                    text = receitasAtivas.size.toString(),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Total de receitas",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Text(
                    text = receitas.size.toString(),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
private fun PrescriptionWarning() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 30.dp,
                vertical = 25.dp
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {

            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MindCarePurple,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = "As receitas abaixo foram enviadas pelo seu " +
                        "profissional de saúde. Para retirar medicamentos " +
                        "controlados, apresente o PDF original na farmácia.",
                color = MindCarePurple,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun PrescriptionCard(
    receita: Prescription,
    onDownloadPdf: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Receita #${receita.number ?: "N/A"}",
                    color = MindCarePurple,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(18.dp))

                if (receita.controlled) {

                    Spacer(modifier = Modifier.width(18.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF0ECFF)
                    ) {

                        Text(
                            text = "Controlado",
                            modifier = Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 7.dp
                            ),
                            color = MindCarePurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                StatusBadge(receita.valid)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                DateBox(
                    title = "Emissão",
                    date = receita.issueDate ?: "Não informada",
                    remainingDays = null,
                    modifier = Modifier.weight(1f)
                )

                DateBox(
                    title = "Vencimento",
                    date = receita.expirationDate ?: "Não informado",
                    remainingDays = receita.daysRemaining,
                    valid = receita.valid,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            ProfessionalBox(
                name = receita.profissional.nomeCompleto ?: "Profissional não informado",
                registration = receita.profissional.registroProfissional ?: "Registro não informado"
            )

            Spacer(modifier = Modifier.height(18.dp))

            MedicinesBox(
                medicines = receita.medicines
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onDownloadPdf,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MindCarePink
                )
            ) {

                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Baixar PDF da Receita",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    valid: Boolean
) {

    val background =
        if (valid) ValidGreenBackground
        else ExpiredBackground

    val color =
        if (valid) ValidGreen
        else ExpiredRed

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = background
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (valid) "Válida" else "Vencida",
                color = Color(0xFF222222),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun DateBox(
    title: String,
    date: String,
    remainingDays: Int? = null,
    valid: Boolean = true,
    modifier: Modifier = Modifier
) {

    val background =
        if (remainingDays != null && valid)
            Color(0xFFF0FCF5)
        else
            Color(0xFFF5F5F5)

    val border =
        if (remainingDays != null && valid)
            Color(0xFFD1F2DE)
        else
            Color(0xFFD9D9D9)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = background,
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Text(
                text = title,
                color = if (remainingDays != null && valid)
                    ValidGreen
                else
                    Color.DarkGray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = date,
                color = if (remainingDays != null && valid)
                    ValidGreen
                else
                    Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (remainingDays != null) {
                Text(
                    text = "$remainingDays dias restantes",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = "",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
@Composable
private fun ProfessionalBox(
    name: String?,
    registration: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF7F7F7)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(35.dp)
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {

                Text(
                    text = name ?: "Profissional não informado",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Text(
                    text = registration ?: "Registro não informado",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
@Composable
private fun MedicinesBox(
    medicines: List<String>
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF4F0FF)
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Text(
                text = "${medicines.size} MEDICAMENTOS PRESCRITOS",
                color = MindCarePurple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            medicines.forEach { medicine ->

                Text(
                    text = "•  ${medicine ?: "Medicamento não informado"}",
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun navigationColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MindCarePink,
        selectedTextColor = MindCarePink,
        unselectedIconColor = MindCarePink,
        unselectedTextColor = MindCarePink,
        indicatorColor = Color.Transparent
    )