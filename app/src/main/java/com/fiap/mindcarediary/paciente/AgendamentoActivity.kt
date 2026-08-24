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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import com.fiap.mindcarediary.BemVindoActivity
import com.fiap.mindcarediary.repository.AuthRepository
import com.fiap.mindcarediary.service.Consulta
import com.fiap.mindcarediary.service.Profissional
import com.fiap.mindcarediary.service.TipoProfissional
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.viewmodel.AgendamentoViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import com.fiap.mindcarediary.viewmodel.PacienteViewModel
import com.fiap.mindcarediary.viewmodel.ProfissionalViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AgendamentoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        enableEdgeToEdge()
        setContent {
            AgendamentoTela(email)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgendamentoTela(
    email: String
) {
    val context = LocalContext.current

    val agendamentoViewModel: AgendamentoViewModel = viewModel()
    val profissionalViewModel: ProfissionalViewModel = viewModel()
    val pacienteViewModel: PacienteViewModel = viewModel()

    val tipoProfissional by agendamentoViewModel.tipoProfissional.collectAsState()
    val profissionais by profissionalViewModel.profissionais.collectAsState()
    val dadosProfissional by profissionalViewModel.dadosProfissional.collectAsState()
    val horarios by agendamentoViewModel.horarios.collectAsState()
    val paciente by pacienteViewModel.paciente.collectAsState()

    var profissionalSelecionado by remember { mutableStateOf<Profissional?>(null) }
    var horarioSelecionado by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    val profissionaisFiltrados = remember(profissionais, tipoProfissional) {
        if (tipoProfissional == null) {
            emptyList()
        } else {
            profissionais.filter {
                it.tipoProfissional == tipoProfissional
            }
        }
    }

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
            pacienteViewModel.loadDadosPaciente(
                email
            )
        }
    }

    LaunchedEffect(profissionalSelecionado) {

        if (tipoProfissional != null && selectedDate != null) {
            agendamentoViewModel.carregarHorariosParaDataInformada(
                nomeUsuario = profissionalSelecionado?.nomeUsuario ?: "",
                dataInformada = selectedDate.toString()
            )
        }
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            selectedDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            horarioSelecionado = null

            tipoProfissional?.let { tipo ->
                agendamentoViewModel.carregarHorariosParaDataInformada(
                    nomeUsuario = profissionalSelecionado?.nomeUsuario!!,
                    dataInformada = selectedDate.toString()
                )
            }
        }
    }

    LaunchedEffect(tipoProfissional) {

        if (tipoProfissional != null) {
            profissionalViewModel.buscarProfissionaisProTipo(
                tipoProfissional = tipoProfissional!!.name)
        }
    }

    LaunchedEffect(profissionalSelecionado) {

        profissionalSelecionado?.let {
            profissionalViewModel.loadDadosProfissional(
                nomeUsuario = it.nomeUsuario
            )
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(email) }
    ) { padding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF5FA))
        ) {
            item {
                HeaderSection(
                    onBackClick = {
                        val intent = Intent(context, InicioPacienteActivity::class.java)
                        context.startActivity(intent)
                    },
                    loginViewModel = loginViewModel
                )
            }


            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Selecione o tipo de profissional")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    TipoProfissionalRadioGroup(
                        selectedTipo = tipoProfissional,
                        onTipoSelected = {
                            agendamentoViewModel.selecionarTipo(it)
                        }
                    )
                }


            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Selecione o profissional")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfissionalDropdown(
                            profissionais = profissionaisFiltrados,
                            profissionalSelecionado = profissionalSelecionado,
                            onProfissionalSelecionado = {
                                profissionalSelecionado = it
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                SectionTitle("Selecione a data")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                SectionTitle("Horários recomendados")

                if (horarios.isEmpty()) {
                    Text(
                        text = "Nenhum horário disponível para a data selecionada.",
                        color = Color(0xFF4F4F6A),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                horarios.forEach { horario ->
                                    val isSelected = horarioSelecionado == horario.dataHoraConsulta

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            horarioSelecionado = horario.dataHoraConsulta
                                        },
                                        label = {
                                            Text(formatHorario(horario.dataHoraConsulta))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val profissionalFinal = profissionalSelecionado ?: dadosProfissional
                        val dateTimeFinal = horarioSelecionado

                        agendamentoViewModel.cadastrarAgendamento(
                            Consulta(
                                profissionalFinal,
                                paciente,
                                false,
                                false,
                                dateTimeFinal
                            )
                        )

                        val intent = Intent(context, InicioPacienteActivity::class.java)
                        intent.putExtra("email", email);
                        context.startActivity(intent)
                    },
                    enabled = profissionalSelecionado != null && horarioSelecionado != null,
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
                        text = "Confirmar Agendamento",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color(0xFF10104A),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun TipoProfissionalRadioGroup(
    selectedTipo: TipoProfissional?,
    onTipoSelected: (TipoProfissional) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedTipo == TipoProfissional.PSICOLOGO,
                onClick = { onTipoSelected(TipoProfissional.PSICOLOGO) }
            )
            Text(
                text = "Psicólogo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedTipo == TipoProfissional.PSIQUIATRA,
                onClick = { onTipoSelected(TipoProfissional.PSIQUIATRA) }
            )
            Text(
                text = "Psiquiatra",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfissionalDropdown(
    profissionais: List<Profissional>,
    profissionalSelecionado: Profissional?,
    onProfissionalSelecionado: (Profissional) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (profissionais.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = profissionalSelecionado?.nomeCompleto ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = profissionais.isNotEmpty(),
            label = { Text("Profissional") },
            placeholder = { Text("Selecione um profissional") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            profissionais.forEach { profissional ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${profissional.nomeCompleto} (${profissional.tipoProfissional})"
                        )
                    },
                    onClick = {
                        onProfissionalSelecionado(profissional)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onBackClick: () -> Unit,
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
            .background(Color(0xFF6EB8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Avatar",
                    tint = Color(0xFF2D63C8),
                    modifier = Modifier.size(46.dp)
                )
            }

            IconButton(onClick = {
                loginViewModel.logout()
                val intent = Intent(context, BemVindoActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color.Black
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Agendar Consulta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10104A)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun BottomNavigationBar(
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

private fun formatHorario(dataHoraConsulta: String): String {
    return runCatching {
        val dateTime = parseDateTime(dataHoraConsulta)
        dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrDefault(
        dataHoraConsulta)
}

private fun parseDateTime(dataHoraConsulta: String): LocalDateTime {
    return runCatching {
        LocalDateTime.parse(dataHoraConsulta)
    }.getOrElse {
        runCatching {
            val time = LocalTime.parse(dataHoraConsulta)
            LocalDateTime.of(LocalDate.now(), time)
        }.getOrElse {
            LocalDateTime.now()
        }
    }
}