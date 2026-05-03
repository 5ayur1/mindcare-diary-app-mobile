package com.fiap.mindcarediary.paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.viewmodel.PacienteViewModel

class InicioPacienteActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            InicioPacienteTela(email)
        }
    }
}

data class DiaryItem(
    val emoji: String,
    val date: String
)

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
fun InicioPacienteTela(email: String) {

    val context = LocalContext.current
    var viewModel: PacienteViewModel = viewModel()

    LaunchedEffect(email) {
        viewModel.loadRegistrosDiarios(email)
    }

    val registrosDiarios by viewModel.registrosDiarios.collectAsState()

    val background = Color(0xFFDDF1FA)
    val pink = Color(0xFFE78BC3)
    val bottomBar = Color(0xFFC8D8F7)
    val dark = Color(0xFF11114A)

    val items = registrosDiarios.map { registro ->
        DiaryItem(
            emoji = converteParaEmoji(registro.nivelHumor),
            date = registro.dataHoraCriacao.split("T")[0]
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {

        // TOPO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    pink,
                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("👩", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "MindCare Diary",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Olá $email,\ncomo você está se sentindo hoje?",
                    color = dark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Sair",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RECENTES
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color(0xFFFF4BA0)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Recentes",
                color = dark,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(items) { item ->
                DiaryCard(item)
            }
        }

        // MENU INFERIOR
        NavigationBar(
            containerColor = bottomBar
        ) {

            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null
                    )
                },
                label = { Text("Início") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF3D9B),
                    selectedTextColor = Color(0xFFFF3D9B),
                    indicatorColor = Color.Transparent
                )
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
                        Icons.Default.Article,
                        contentDescription = null
                    )
                },
                label = { Text("Diário") },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color(0xFFFF3D9B),
                    unselectedTextColor = Color(0xFFFF3D9B),
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                label = { Text("Relatório") },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color(0xFFFF3D9B),
                    unselectedTextColor = Color(0xFFFF3D9B),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun DiaryCard(item: DiaryItem) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = item.emoji,
                fontSize = 34.sp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {

                Text(
                    text = item.date,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF11114A),
                    fontSize = 18.sp
                )

                Text(
                    text = "Diário Completo",
                    color = Color(0xFF11114A),
                    fontSize = 15.sp
                )
            }
        }
    }
}