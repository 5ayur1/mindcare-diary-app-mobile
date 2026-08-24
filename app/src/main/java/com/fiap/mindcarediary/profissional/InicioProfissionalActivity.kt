package com.fiap.mindcarediary.profissional

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
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
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
import com.fiap.mindcarediary.LoginActivity
import com.fiap.mindcarediary.paciente.InicioPacienteActivity
import com.fiap.mindcarediary.repository.AuthRepository
import com.fiap.mindcarediary.service.Paciente
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import com.fiap.mindcarediary.viewmodel.ProfissionalViewModel

class InicioProfissionalActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            PainelProfissionalTela(email)
        }
    }
}

@Composable
fun PainelProfissionalTela(email: String) {

    val profissionalViewModel: ProfissionalViewModel = viewModel()

    val pacientes by profissionalViewModel.pacientes.collectAsState();

    val pacientesAtivo = pacientes.filter({
        p -> p.ativo
    })

    val pacientesAtencao = pacientes.filter({
            p -> "ATENCAO".equals(p.estadoPaciente)
    })

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

    LaunchedEffect(email) {
        if (email != null) {
            profissionalViewModel.loadPacientes(email)
        }
    }

    Scaffold() { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFEAF6FF))
            ) {

                HeaderSection(loginViewModel)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Total",
                        value = pacientesAtivo.size,
                        subtitle = "Pacientes ativos",
                        titleColor = Color(0xFF9C27B0)
                    )

                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Atenção",
                        value = pacientesAtencao.size,
                        subtitle = "Precisam de atenção",
                        titleColor = Color(0xFFFF6B6B)
                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Meus Pacientes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF11114D),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(pacientes) { paciente ->
                        PacienteCard(paciente, email)
                    }
                }
            }
    }
}

@Composable
private fun HeaderSection(
    loginViewModel: LoginViewModel
) {

    val context = LocalContext.current

    Surface(
        color = Color(0xFFB57BE2),
        shape = RoundedCornerShape(
            bottomStart = 32.dp,
            bottomEnd = 32.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍⚕️", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    loginViewModel.logout()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Painel Profissional",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF11114D)
            )

            Text(
                text = "Acompanhe seus pacientes",
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar paciente...")
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    subtitle: String,
    titleColor: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                color = titleColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PacienteCard(
    paciente: Paciente,
    email: String
) {
    val context = LocalContext.current

    val statusColor = when (paciente.estadoPaciente) {
        "MELHORANDO" -> Color(0xFF27C84D)
        "ESTAVEL" -> Color(0xFF4A90E2)
        "ATENCAO" -> Color(0xFFFF5A5A)
        else -> Color(0xFFFF5A5A)
    }

    Card(
        onClick = {
            val intent = Intent(context, DiarioProfissionalActivity::class.java)
            intent.putExtra("pacienteNomeUsuario", paciente.nomeUsuario);
            intent.putExtra("email", email)
            context.startActivity(intent)
        },
        shape = RoundedCornerShape(24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFEFEF)),
                contentAlignment = Alignment.Center
            ) {
                Text("👨‍️", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = paciente.nomeCompleto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF11114D)
                )
            }

            Text(
                text = paciente.estadoPaciente,
                color = statusColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}