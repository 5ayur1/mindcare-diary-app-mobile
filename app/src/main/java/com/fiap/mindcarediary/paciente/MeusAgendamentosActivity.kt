package com.fiap.mindcarediary.paciente;

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.service.Consulta
import com.fiap.mindcarediary.viewmodel.AgendamentoViewModel

private val MindCareBlue = Color(0xFF65BDF0)
private val MindCareBackground = Color(0xFFE1F4FD)
private val MindCareBottomBar = Color(0xFFC5D8FF)
private val MindCarePink = Color(0xFFE9429D)
private val MindCareDarkBlue = Color(0xFF081653)
private val MindCarePurple = Color(0xFF6D4BC3)

private val StatusGreen = Color(0xFFE2F7E9)
private val StatusGreenText = Color(0xFF16833B)

private val StatusRed = Color(0xFFFFE5E5)
private val StatusRedText = Color(0xFFD32F2F)

class MeusAgendamentosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            MeusAgendamentosTela(email)
        }
    }
}
@Composable
fun MeusAgendamentosTela(
    email: String,
) {

    val agendamentoViewModel: AgendamentoViewModel = viewModel()

    val agendamentos by agendamentoViewModel.agendamentos.collectAsState();

    var filtroSelecionado by remember {
        mutableStateOf<FiltroAgendamento>(FiltroAgendamento.TODOS)
    }

    val agendamentosExibidos = when (filtroSelecionado) {

        FiltroAgendamento.TODOS ->
            agendamentos

        FiltroAgendamento.AGENDADAS ->
            agendamentos.filter {
                !it.atendida && !it.cancelada
            }

        FiltroAgendamento.CANCELADAS ->
            agendamentos.filter {
                !it.atendida && it.cancelada
            }
    }

    val context = LocalContext.current

    LaunchedEffect(email) {
        if (email.isNotBlank()) {
            agendamentoViewModel.loadAgendamentos(
                email,
                onSuccess = { mensagem ->
                    Toast.makeText(
                        context,
                        mensagem,
                        Toast.LENGTH_LONG
                    ).show()
                },
                onError = { mensagem ->
                    Toast.makeText(
                        context,
                        mensagem,
                        Toast.LENGTH_LONG
                    ).show()
            })
        }
    }

    Scaffold(
        containerColor = MindCareBlue,
        bottomBar = {
            BottomNavigationMindCare(email)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF6FC))
        ) {

            HeaderAgendamentos(
                onVoltar = {
                    val intent = Intent(context, InicioPacienteActivity::class.java)
                    intent.putExtra("email", email)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            FiltroAgendamentos(
                filtroSelecionado = filtroSelecionado,
                agendamentos = agendamentos,
                onFiltroChanged = {
                    filtroSelecionado = it
                }
            )

            if (agendamentosExibidos.isEmpty()) {

                EmptyAgendamentos()

            } else {

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = agendamentosExibidos,
                        key = {
                            it.number!!
                        }
                    ) { agendamento ->

                        AgendamentoCard(
                            agendamento = agendamento,
                            onClick = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderAgendamentos(
    onVoltar: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                MindCareBlue,
                RoundedCornerShape(
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp
                )
            )
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 18.dp,
                bottom = 20.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onVoltar
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(48.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Meus Agendamentos",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MindCareDarkBlue
        )

        Text(
            text = "Consulte suas consultas",
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 14.sp,
            color = MindCareDarkBlue
        )
    }
}

enum class FiltroAgendamento {
    TODOS,
    AGENDADAS,
    CANCELADAS
}

@Composable
private fun FiltroAgendamentos(
    filtroSelecionado: FiltroAgendamento,
    agendamentos: List<Consulta>,
    onFiltroChanged: (FiltroAgendamento) -> Unit
) {

    val quantidadeAgendadas =
        agendamentos.count {
            !it.atendida && !it.cancelada
        }

    val quantidadeCanceladas =
        agendamentos.count {
            it.cancelada
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
    ) {

        Text(
            text = "Agendamentos",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MindCareDarkBlue
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FiltroButton(
                text = "Todos",
                selected = filtroSelecionado == FiltroAgendamento.TODOS,
                modifier = Modifier.weight(1f),
                onClick = {
                    onFiltroChanged(FiltroAgendamento.TODOS)
                }
            )

            FiltroButton(
                text = "Agendadas ($quantidadeAgendadas)",
                selected = filtroSelecionado == FiltroAgendamento.AGENDADAS,
                modifier = Modifier.weight(1f),
                onClick = {
                    onFiltroChanged(FiltroAgendamento.AGENDADAS)
                }
            )

            FiltroButton(
                text = "Canceladas ($quantidadeCanceladas)",
                selected = filtroSelecionado == FiltroAgendamento.CANCELADAS,
                modifier = Modifier.weight(1f),
                onClick = {
                    onFiltroChanged(FiltroAgendamento.CANCELADAS)
                }
            )
        }
    }
}


@Composable
private fun FiltroButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier
            .height(42.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        color = if (selected)
            MindCarePink
        else
            Color.White
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected)
                    Color.White
                else
                    MindCareDarkBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AgendamentoCard(
    agendamento: Consulta,
    onClick: () -> Unit
) {

    val cancelada =
        agendamento.cancelada

    val statusBackground =
        if (cancelada)
            StatusRed
        else
            StatusGreen

    val statusText =
        if (cancelada)
            StatusRedText
        else
            StatusGreenText

    val statusLabel =
        if (cancelada)
            "Cancelada"
        else
            "Agendada"

    val dataConsulta = agendamento.dataHoraConsulta!!.split("T")[0]
    val horaConsulta = agendamento.dataHoraConsulta!!.split("T")[1]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (cancelada)
                            StatusRed
                        else
                            Color(0xFFEDE7FF)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        if (cancelada)
                            Icons.Default.Cancel
                        else
                            Icons.Default.CalendarMonth,

                    contentDescription = null,

                    tint =
                        if (cancelada)
                            StatusRedText
                        else
                            MindCarePurple,

                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MindCarePurple,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = dataConsulta,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MindCareDarkBlue
                    )
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MindCarePurple,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = horaConsulta,
                        fontSize = 13.sp,
                        color = MindCareDarkBlue
                    )
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = agendamento.profissional!!.nomeCompleto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16172D)
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = buildString {

                        append(agendamento.profissional.tipoProfissional)

                        agendamento.profissional.registroProfissional?.let {

                            append(" • ")
                            append(it)
                        }
                    },
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            // ---------------------------------------------
            // STATUS + SETA
            // ---------------------------------------------

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Surface(
                    color = statusBackground,
                    shape = RoundedCornerShape(10.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusText)
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Text(
                            text = statusLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Detalhes",
                    tint = Color.Gray,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyAgendamentos() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 30.dp,
                vertical = 60.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFD2E7FF)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MindCarePurple,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Nenhum agendamento",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MindCareDarkBlue
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = "Você não possui consultas agendadas ou canceladas.",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun BottomNavigationMindCare(
    email: String
) {
    NavigationBar(
        containerColor = Color(0xFFD8E4FF),
    ) {

        val context = LocalContext.current

        NavigationBarItem(
            selected = true,
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
private fun BottomItem(
    icon: ImageVector,
    text: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MindCarePink,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = text,
            fontSize = 10.sp,
            color = MindCarePink
        )
    }
}