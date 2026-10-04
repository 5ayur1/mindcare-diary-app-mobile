package com.fiap.mindcarediary.paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import com.fiap.mindcarediary.viewmodel.PacienteViewModel

class DiarioPacienteActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setContent {
            DiarioPacienteTela(this)
        }
    }
}

@Composable
fun DiarioPacienteTela(
    activity: DiarioPacienteActivity = DiarioPacienteActivity(),

) {

    val editor: com.fiap.mindcarediary.viewmodel.DiarioEditorViewModel = viewModel()
    val email = activity.intent?.getStringExtra("email") ?: "Unknown"

    val background = Color(0xFFDDF1FA)
    val pink = Color(0xFFE78BC3)
    val blue = Color(0xFF1E88E5)

    val editorState by editor.state.collectAsState()
    val selectedMood = editorState.humor
    val saving = editorState.salvando
    val saved = editorState.salvo
    val saveError = editorState.erro
    val positiveText = editorState.positivo
    val negativeText = editorState.negativo
    val context = LocalContext.current
    var confirmDiscard by remember { mutableStateOf(false) }
    LaunchedEffect(email) { editor.bind(email, com.fiap.mindcarediary.repository.ProtectedDraftStore(context, email, "diario")) }
    if (!editorState.pronto) { CircularProgressIndicator(); return }
    if (confirmDiscard) AlertDialog(onDismissRequest = { confirmDiscard = false },
        title = { Text("Descartar rascunho?") }, text = { Text("O texto local será removido. Se tentou salvar, consulte o histórico.") },
        confirmButton = { TextButton(onClick = { editor.descartar(); confirmDiscard = false }) { Text("Descartar") } },
        dismissButton = { TextButton(onClick = { confirmDiscard = false }) { Text("Cancelar") } })
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
        bottomBar = { BottomMenuDiario(email) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(background)
        ) {

            TopMenuDiario(pink, loginViewModel)

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {

                item {
                    Text("Escolha escrever abaixo ou conversar com a assistente de registro.")
                    Button(onClick = {
                        context.startActivity(Intent(context, ChatMiaActivity::class.java).putExtra("email", email))
                    }, enabled = !saving, modifier = Modifier.fillMaxWidth()) {
                        Text("Chat com a MIA")
                    }
                    Text("Diário Tradicional", fontWeight = FontWeight.Bold)
                    Text("Rascunho protegido neste aparelho; removido ao sair da conta.")
                    editorState.aviso?.let { Text(it) }
                    TextButton(onClick = { confirmDiscard = true }, enabled = !saving && !saved) { Text("Descartar rascunho") }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // HUMOR
                item {
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
                                MoodItem("😄", "OTIMO", selectedMood) { editor.editar(humor = it) }
                                MoodItem("🙂", "BOM", selectedMood) { editor.editar(humor = it) }
                                MoodItem("😐", "NEUTRO", selectedMood) { editor.editar(humor = it) }
                                MoodItem("☹️", "MAL", selectedMood) { editor.editar(humor = it) }
                                MoodItem("😭", "PESSIMO", selectedMood) { editor.editar(humor = it) }
                            }
                        }
                    }
                }



                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // POSITIVO
                item {
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
                                onValueChange = { editor.editar(positivo = it) },
                                enabled = !saving && !saved,
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
                }


                item {


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
                                onValueChange = { editor.editar(negativo = it) },
                                enabled = !saving && !saved,
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
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    // BOTÃO
                    Button(
                        enabled = !saving && !saved,
                        onClick = {
                            editor.salvar()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (saving) "Salvando…" else if (saved) "Diário salvo" else "Salvar Diário")
                    }
                    saveError?.let { Text(it, color = Color.Red) }
                    if (saved) Text("Seu diário foi salvo. Você pode consultá-lo no início.")
                }
            }
        }
    }
}

@Composable
fun TopMenuDiario(
    pink: Color,
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
                Icon(
                    imageVector = Icons.Default.Save,
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
                    text = "Meu Diário",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BottomMenuDiario(
    email: String
) {

    val context = LocalContext.current

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
                selectedIconColor = Color(0xFFFF3D9B),
                selectedTextColor = Color(0xFFFF3D9B),
                indicatorColor = Color.Transparent
            )
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

