package com.fiap.mindcarediary.paciente

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.fiap.mindcarediary.service.RelatorioSemanal
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import com.fiap.mindcarediary.viewmodel.RelatorioViewModel

class RelatorioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            RelatorioTela(email)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RelatorioTela(
    email: String
) {

    val relatorioViewModel: RelatorioViewModel = viewModel()

    val relatorios by relatorioViewModel.relatorios.collectAsState();

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
            relatorioViewModel.carregarRelatoriosSemanais(email)
        }
    }

    Scaffold(
        bottomBar = {
            BottomMenuRelatorio(email)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF6FC))
        ) {

            HeaderRelatorio(loginViewModel)

            Spacer (
                modifier = Modifier.height(20.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(relatorios.size) { index ->

                    RelatorioCard(
                        relatorio = relatorios[index]
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderRelatorio(
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
            .background(Color(0xFF6DB6F2))
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
                    text = "Relatórios Semanais",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A0A54)
                )
            }
        }
    }
}

@Composable
fun RelatorioCard(
    relatorio: RelatorioSemanal
) {
    val quantidadePositivos = relatorio.totalPositivos
    val quantidadeNegativos = relatorio.totalNegativos
    val rangeDates = relatorio.faixaDeDatas.split("^")
    val initialDate = rangeDates[0]
    val finalDate = rangeDates[1]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "$initialDate :: $finalDate",
                color = Color(0xFFA020F0),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Resumo da Semana",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                IndicadorCard(
                    titulo = "Positivos",
                    valor = quantidadePositivos,
                    background = Color(0xFFE4F8E8),
                    border = Color(0xFF80D69D),
                    modifier = Modifier.weight(1f)
                )

                IndicadorCard(
                    titulo = "Desafios",
                    valor = quantidadeNegativos,
                    background = Color(0xFFFFEFE9),
                    border = Color(0xFFFFB0A8),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            ObservacaoCard(
                observacao = relatorio.observacoes,
                recomendacao = relatorio.recomendacoes
            )
        }
    }
}

@Composable
fun IndicadorCard(
    titulo: String,
    valor: Int,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        border = BorderStroke(
            2.dp,
            border
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = titulo,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = valor.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ObservacaoCard(
    observacao: String,
    recomendacao: String
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0ECFF)
        ),
        border = BorderStroke(
            2.dp,
            Color(0xFFC7B7FF)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {

            Text(
                text = "Observações do Profissional",
                color = Color(0xFF5D39D9),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(observacao)

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Card {

                Text(
                    text = "Recomendação:\n$recomendacao",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF5D39D9)
                )
            }
        }
    }
}

@Composable
fun BottomMenuRelatorio(email: String) {

    NavigationBar(
        containerColor = Color(0xFFDCE7FF)
    ) {

        val context = LocalContext.current

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, InicioPacienteActivity::class.java)
                intent.putExtra("email", email)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    null
                )
            },
            label = {
                Text("Início")
            }
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
                    null
                )
            },
            label = {
                Text("Diário")
            }
        )

        NavigationBarItem(
            selected = true,
            onClick = {
                val intent = Intent(context, RelatorioActivity::class.java)
                intent.putExtra("email", email)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    Icons.Default.Description,
                    null
                )
            },
            label = {
                Text("Relatório")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF3D9B),
                selectedTextColor = Color(0xFFFF3D9B),
                indicatorColor = Color.Transparent
            )
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