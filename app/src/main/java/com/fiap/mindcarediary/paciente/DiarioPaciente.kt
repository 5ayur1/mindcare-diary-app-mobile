package com.fiap.mindcarediary.paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.viewmodel.PacienteViewModel

class DiarioPacienteActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiarioPacienteTela(this)
        }
    }
}

@Composable
fun DiarioPacienteTela(
    activity: DiarioPacienteActivity = DiarioPacienteActivity(),
    viewModel: PacienteViewModel = viewModel()
) {

    val email = activity.intent?.getStringExtra("email") ?: "Unknown"

    val background = Color(0xFFDDF1FA)
    val pink = Color(0xFFE78BC3)
    val blue = Color(0xFF1E88E5)

    var selectedMood by remember { mutableStateOf("Bem") }
    var positiveText by remember { mutableStateOf("") }
    var negativeText by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    pink,
                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                modifier = Modifier.clickable { { } }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Meu Diário",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        ) {

            // HUMOR
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Como você se sentiu hoje?",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MoodItem("😄", "OTIMO", selectedMood) { selectedMood = it }
                        MoodItem("🙂", "BOM", selectedMood) { selectedMood = it }
                        MoodItem("😐", "NEUTRO", selectedMood) { selectedMood = it }
                        MoodItem("☹️", "MAL", selectedMood) { selectedMood = it }
                        MoodItem("😭", "PESSIMO", selectedMood) { selectedMood = it }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // POSITIVO
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfied,
                            contentDescription = null,
                            tint = Color(0xFF2ECC71)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Pontos Positivos do Dia",
                            color = Color(0xFF2ECC71),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = positiveText,
                        onValueChange = { positiveText = it },
                        placeholder = {
                            Text("O que aconteceu de bom hoje? Quais momentos te deixaram feliz?")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NEGATIVO
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Dificuldades e Desafios",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = negativeText,
                        onValueChange = { negativeText = it },
                        placeholder = {
                            Text("O que te incomodou hoje? Houve algum momento difícil?")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÃO
            Button(
                onClick = {
                    viewModel.cadastrarRegistroDiario(
                        RegistroDiario(
                            selectedMood,
                            positiveText,
                            negativeText,
                            ""
                        ), email)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salvar Diário")
            }
        }

        // BOTTOM BAR
        NavigationBar(
            containerColor = Color(0xFFC8D8F7)
        ) {
            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(context, InicioPacienteActivity::class.java)
                    intent.putExtra("email", email);
                    context.startActivity(intent)
                },
                icon = { Icon(Icons.Default.CalendarMonth, null) },
                label = { Text("Início") }
            )
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = { Icon(Icons.Default.Article, null) },
                label = { Text("Diário") }
            )
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Icon(Icons.Default.Lock, null) },
                label = { Text("Relatório") }
            )
        }
    }
}

@Composable
fun MoodItem(
    emoji: String,
    label: String,
    selected: String,
    onSelect: (String) -> Unit
) {

    val isSelected = label == selected

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onSelect(label) }
            .padding(4.dp)
    ) {

        Text(
            text = emoji,
            fontSize = if (isSelected) 32.sp else 28.sp
        )

        Text(
            text = label,
            fontSize = 12.sp
        )
    }
}

