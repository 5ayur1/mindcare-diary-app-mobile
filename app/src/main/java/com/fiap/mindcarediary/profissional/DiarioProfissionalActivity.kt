package com.fiap.mindcarediary.profissional

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.fiap.mindcarediary.paciente.converteParaEmoji
import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.viewmodel.PacienteViewModel
import com.fiap.mindcarediary.viewmodel.RelatorioViewModel

class DiarioProfissionalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pacienteNomeUsuario = intent.getStringExtra("pacienteNomeUsuario") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            DiarioProfissionalTela(pacienteNomeUsuario, email)
        }
    }
}

fun converteParaEmoji(nivelHumor: String): String {
    when (nivelHumor) {
        "OTIMO" -> return "😄"
        "BOM" -> return "🙂"
        "NEUTRO" -> return "😐"
        "MAL" -> return "☹️"
        "PESSIMO" -> return "😭"
    }
    return "-"
}

@Composable
fun DiarioProfissionalTela(
    pacienteNomeUsuario: String,
    email: String
) {

    var abaSelecionada by remember { mutableStateOf(0) }

    val pacienteViewModel: PacienteViewModel = viewModel()

    val relatorioViewModel: RelatorioViewModel = viewModel()

    val registrosDiarios by pacienteViewModel.registrosDiarios.collectAsState()

    val paciente by pacienteViewModel.paciente.collectAsState()

    val registros = registrosDiarios

    val context = LocalContext.current

    LaunchedEffect(pacienteNomeUsuario) {
        if (pacienteNomeUsuario != null) {
            relatorioViewModel.gerarRelatorioSemanal(pacienteNomeUsuario)
        }
    }

    LaunchedEffect(pacienteNomeUsuario) {
        if (pacienteNomeUsuario != null) {
            pacienteViewModel.loadRegistrosDiarios(pacienteNomeUsuario)
        }
    }

    LaunchedEffect(pacienteNomeUsuario) {
        if (pacienteNomeUsuario != null) {
            pacienteViewModel.loadDadosPaciente(pacienteNomeUsuario)
        }
    }

    Scaffold() { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE8F4FC))
        ) {

            HeaderPaciente(
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
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {

                    Text(
                        text = "Entradas do Diário",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF11114D)
                    )
                }

                items(registros) { registro ->

                    RegistroCard(registro)
                }

                item {
                    Button(
                        onClick = {
                            relatorioViewModel.gerarRelatorioSemanal(pacienteNomeUsuario)
                            val intent = Intent(context, RelatorioProfissionalActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("pacienteNomeUsuario", pacienteNomeUsuario)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE64AA6),
                            disabledContainerColor = Color(0xFFE64AA6).copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .padding(horizontal = 40.dp)
                    ) {
                        Text(
                            text = "Gerar Relatório",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderPaciente(
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
                        text = "Histórico e relatórios",
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
            }
        }
    }
}

@Composable
private fun RegistroCard(
    registro: RegistroDiario
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            2.dp,
            Color(0xFFB04DE6)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8E0FF)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = converteParaEmoji(registro.nivelHumor),
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = registro.dataHoraCriacao.split("T")[0],
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )

                    Text(
                        text = registro.nivelHumor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Pontos Positivos",
                        color = Color(0xFF2DC653),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = registro.pontosPositivos
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Desafios",
                        color = Color(0xFFFF5A5A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = registro.dificuldadesDesafios
                    )
                }
            }
        }
    }
}