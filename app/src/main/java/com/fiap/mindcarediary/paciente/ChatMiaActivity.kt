package com.fiap.mindcarediary.paciente

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.fiap.mindcarediary.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.ui.theme.MindcareDiaryTheme
import com.fiap.mindcarediary.viewmodel.ChatViewModel

class ChatMiaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setContent {
            MindcareDiaryTheme(darkTheme = false, dynamicColor = false) {
                ChatMiaScreen(onClose = { finish() }, onHistory = {
                    startActivity(Intent(this, InicioPacienteActivity::class.java).apply {
                        putExtra("email", intent.getStringExtra("email"))
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    finish()
                })
            }
        }
    }
}

@Composable
fun ChatMiaScreen(onClose: () -> Unit, onHistory: () -> Unit, model: ChatViewModel = viewModel()) {
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(
        primary = Color(0xFF1E88E5), onPrimary = Color.White,
        primaryContainer = Color(0xFFE78BC3), onPrimaryContainer = Color(0xFF11114A),
        onSurface = Color(0xFF11114A), onSurfaceVariant = Color(0xFF44446A)
    )) {
        ChatMiaContent(onClose, onHistory, model)
    }
}

@Composable
private fun ChatMiaContent(onClose: () -> Unit, onHistory: () -> Unit, model: ChatViewModel) {
    val state by model.state.collectAsStateWithLifecycle()
    var confirmExit by remember { mutableStateOf(false) }
    var speechHint by remember { mutableStateOf<String?>(null) }
    val close = { if (state.hasDraft && !state.saved) confirmExit = true else onClose() }
    BackHandler { close() }
    val scroll = rememberLazyListState()
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) scroll.animateScrollToItem(state.messages.lastIndex)
    }

    Scaffold(containerColor = Color(0xFFDDF1FA)) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)).background(Color(0xFFE78BC3)).padding(vertical = 12.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = close) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF11114A)) }
                Image(painter = painterResource(R.drawable.mia_profile), contentDescription = "Foto de perfil da MIA",
                    contentScale = ContentScale.Crop, modifier = Modifier.size(52.dp).clip(CircleShape))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("MIA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF11114A))
                    Text("MindCare Intelligent Assistent", style = MaterialTheme.typography.bodySmall, color = Color(0xFF11114A))
                }
            }
            when {
                state.saved -> Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Registro salvo no seu diário.", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = onHistory) { Text("Ver meu histórico") }
                }
                state.reviewing -> Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Revise seu registro", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primaryContainer)
                    Text("Somente suas falas aparecem abaixo. Edite o texto antes de confirmar. As perguntas da MIA não serão salvas.")
                    OutlinedTextField(value = state.confirmedText, onValueChange = model::editReview,
                        enabled = !state.saveAttempted, label = { Text("Texto do diário") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledBorderColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledLabelColor = MaterialTheme.colorScheme.primaryContainer,
                            cursorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth(), minLines = 6,
                        supportingText = { Text("${state.confirmedText.length}/20000") })
                    Text("Como você se sente? (opcional)")
                    listOf("SEM_DEFINICAO" to "Prefiro não informar", "OTIMO" to "Ótimo", "BOM" to "Bom", "NEUTRO" to "Neutro", "MAL" to "Mal", "PESSIMO" to "Péssimo").forEach { (value, label) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = state.mood == value, onClick = { model.selectMood(value) }, enabled = !state.saveAttempted)
                            Text(label)
                        }
                    }
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    if (state.saving) LinearProgressIndicator(Modifier.fillMaxWidth())
                    Button(onClick = model::save, enabled = !state.saving && state.confirmedText.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                        Text(if (state.saveAttempted) "Tentar confirmar novamente" else "Confirmar e salvar diário")
                    }
                    TextButton(onClick = model::cancelReview, enabled = !state.saveAttempted) { Text("Voltar à conversa") }
                }
                else -> {
                    Text("A MIA ajuda a registrar seu dia; não oferece orientação clínica. O rascunho só será salvo após sua confirmação.",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), style = MaterialTheme.typography.bodySmall, color = Color(0xFF555555))
                    LazyColumn(state = scroll, modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.messages, key = { it.id }) { message ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.patient) Arrangement.End else Arrangement.Start) {
                                Card(modifier = Modifier.widthIn(max = 320.dp), shape = RoundedCornerShape(22.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (message.patient) Color(0xFFC8D8F7) else Color.White)) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(if (message.patient) "Você" else "MIA", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF11114A))
                                        Text(message.text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF11114A))
                                    }
                                }
                            }
                        }
                    }
                    if (state.sending) LinearProgressIndicator(Modifier.fillMaxWidth())
                    state.error?.let { Text(it, Modifier.padding(horizontal = 12.dp), color = MaterialTheme.colorScheme.error) }
                    if (state.pendingReply != null && !state.sending) {
                        TextButton(onClick = model::retryReply) { Text("Tentar resposta novamente") }
                    }
                    Text("${state.input.length}/4000", Modifier.padding(start = 28.dp, top = 6.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = state.input, onValueChange = model::updateInput,
                            placeholder = { Text("Digite aqui ou use o gravador de voz", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.weight(1f).semantics { contentDescription = "Mensagem" }, maxLines = 4,
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                            enabled = !state.recognizingSpeech && !state.sending && state.pendingReply == null,
                            trailingIcon = {
                                IconButton(onClick = model::send, enabled = state.input.isNotBlank() && !state.recognizingSpeech && !state.sending && state.pendingReply == null) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Enviar",
                                        tint = Color.White, modifier = Modifier.size(24.dp).background(Color(0xFFF238A3), CircleShape).padding(3.dp))
                                }
                            })
                        HoldSpeechButton(model, enabled = !state.sending && state.pendingReply == null, onHint = { speechHint = it })
                    }
                    Column(Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 6.dp)) {
                        Text(speechHint ?: if (state.recognizingSpeech) "Ouvindo… solte o microfone para concluir." else "Segure o botão de microfone para gravar o áudio. Revise a transcrição antes de clicar em Enviar.",
                            style = MaterialTheme.typography.bodySmall, color = Color(0xFF555555))
                    }
                    Button(onClick = model::review, enabled = !state.recognizingSpeech && !state.sending && state.messages.any { it.patient },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5E0F5), contentColor = Color(0xFF11114A), disabledContainerColor = Color(0xFFB5E0F5), disabledContentColor = Color(0xFF737373)),
                        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 12.dp).heightIn(min = 44.dp)) { Text("Revisar e salvar registro") }
                }
            }
        }
    }
    if (confirmExit) AlertDialog(onDismissRequest = { confirmExit = false },
        title = { Text("Sair do chat?") },
        text = { Text(if (state.saveAttempted) "O resultado do salvamento pode estar pendente. Consulte seu histórico antes de criar outro registro." else "O rascunho fica apenas nesta sessão. Ao sair, o texto não salvo será descartado.") },
        confirmButton = { TextButton(onClick = onClose) { Text("Sair") } },
        dismissButton = { TextButton(onClick = { confirmExit = false }) { Text("Continuar") } })
}
