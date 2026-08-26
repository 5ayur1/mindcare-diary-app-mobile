package com.fiap.mindcarediary.paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
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
import com.fiap.mindcarediary.repository.AuthRepository
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import com.fiap.mindcarediary.viewmodel.PacienteViewModel
import kotlin.jvm.java

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

    var viewModel: PacienteViewModel = viewModel()

    val registrosDiarios by viewModel.registrosDiarios.collectAsState()

    LaunchedEffect(email) {
        viewModel.loadRegistrosDiarios(email)
    }

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

    val context = LocalContext.current

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

    Scaffold(
        bottomBar = {
            BottomMenuInicio(bottomBar, email)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(background)
        ) {

            TopMenuInicio(email, pink, dark, loginViewModel)

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ActionButton(
                    text = "Agendar Consulta",
                    color = Color(0xFF65BDF0),
                    email = email
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ActionButton(
                    text = "Meus Agendamentos",
                    color = pink,
                    email = email
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

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


        }
    }
}

@Composable
fun TopMenuInicio(
    email: String,
    pink: Color,
    dark: Color,
    loginViewModel: LoginViewModel)
{
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
            .background(pink)
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

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👩", fontSize = 24.sp)
                }

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
                    text = "Olá $email,\ncomo você está se sentindo hoje?",
                    color = dark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}


@Composable
fun BottomMenuInicio(
    bottomBar: Color,
    email: String
){

    val context = LocalContext.current

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
                    Icons.Default.Lock,
                    contentDescription = null
                )
            },
            label = { Text("Relatório") }
        )

        NavigationBarItem(
            selected = false,
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
            label = { Text("Prescrição") }
        )
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
@Composable
fun ActionButton(
    text: String,
    color: Color,
    email: String
) {

    val context = LocalContext.current

    Button(
        onClick = {
            if("Agendar Consulta".equals(text)) {
                val intent = Intent(context, AgendamentoActivity::class.java)
                intent.putExtra("email", email);
                context.startActivity(intent)
            } else if("Meus Agendamentos".equals(text)) {
                val intent = Intent(context, MeusAgendamentosActivity::class.java)
                intent.putExtra("email", email);
                context.startActivity(intent)
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(60.dp),
    ) {

        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}