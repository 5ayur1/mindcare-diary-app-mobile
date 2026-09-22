package com.fiap.mindcarediary

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fiap.mindcarediary.service.*
import com.fiap.mindcarediary.ui.theme.MindcareDiaryTheme
import kotlinx.coroutines.*

class PerfilPrivacidadeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent { MindcareDiaryTheme(darkTheme = false, dynamicColor = false) { PerfilPrivacidade { finish() } } }
    }
}

@Composable
fun PerfilPrivacidade(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var document by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var confirm by remember { mutableStateOf(false) }
    var closed by remember { mutableStateOf<EncerramentoResponse?>(null) }
    BackHandler(enabled = busy || closed != null) {
        if (closed != null) context.startActivity(Intent(context, BemVindoActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri == null) { busy = false; password = "" }
        else scope.launch {
            try {
                val secret = password; password = ""
                val response = RetrofitClient.api.exportarConta(ConfirmacaoConta(secret))
                withContext(Dispatchers.IO) {
                    response.use { body ->
                        context.contentResolver.openOutputStream(uri)?.use { out -> body.byteStream().use { it.copyTo(out) } }
                            ?: error("Arquivo indisponível")
                    }
                }
                message = "Cópia salva no local escolhido. O ZIP contém dados pessoais; guarde-o em local privado."
            } catch (e: CancellationException) {
                withContext(NonCancellable + Dispatchers.IO) { runCatching { context.contentResolver.delete(uri, null, null) } }
                throw e
            } catch (e: Exception) {
                withContext(Dispatchers.IO) { runCatching { context.contentResolver.delete(uri, null, null) } }
                message = "Não foi possível exportar. Confira sua senha, conexão e sessão e tente novamente."
            } finally { busy = false }
        }
    }
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onBack, enabled = !busy && closed == null) { Text("Voltar") }
            Text("Meu perfil e privacidade", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { document = "termos.txt" }) { Text("Termos de Uso") }
            TextButton(onClick = { document = "privacidade.txt" }) { Text("Política de Privacidade") }
            Text("Contato: mindcare.diary@gmail.com")
            Text("Exportar meus dados", style = MaterialTheme.typography.titleMedium)
            Text("Baixe um ZIP com seus registros do diário e Chat confirmado, relatórios, consultas e prescrições disponíveis. Contas profissionais exportam seus próprios dados e aceites, sem os dados de pacientes.")
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Confirme sua senha") },
                visualTransformation = PasswordVisualTransformation(), singleLine = true, enabled = !busy && closed == null, modifier = Modifier.fillMaxWidth())
            Button(enabled = password.isNotBlank() && !busy && closed == null, onClick = { busy = true; message = null; export.launch("mindcare-meus-dados.zip") }) { Text("Exportar dados em ZIP") }
            HorizontalDivider()
            Text("Excluir minha conta", style = MaterialTheme.typography.titleMedium)
            Text("O acesso será encerrado imediatamente e um pedido de eliminação será registrado. Seus registros não serão apagados automaticamente: documentos clínicos podem exigir conservação. Exporte sua cópia antes de continuar.")
            OutlinedButton(enabled = password.isNotBlank() && !busy && closed == null, onClick = { confirm = true }) { Text("Excluir conta e solicitar eliminação") }
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            message?.let { Text(it) }
        }
    }
    document?.let { LegalDocumentDialog(it) { document = null } }
    if (confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text("Encerrar seu acesso?") },
        text = { Text("Você sairá da conta e não poderá entrar novamente. A eliminação dos dados será analisada separadamente. Esta ação não cancela automaticamente consultas nem transfere a guarda de documentos clínicos.") },
        confirmButton = { TextButton(onClick = {
            confirm = false; busy = true; message = null
            scope.launch {
                try {
                    val secret = password; password = ""
                    val result = RetrofitClient.api.encerrarConta(ConfirmacaoConta(secret))
                    closed = result
                    TokenManager(context.applicationContext).clearToken()
                } catch (e: CancellationException) { throw e }
                catch (e: Exception) { message = "Não foi possível confirmar o encerramento. Se perdeu acesso, contate mindcare.diary@gmail.com para verificar o pedido; caso contrário, confira sua senha e tente novamente." }
                finally { busy = false }
            }
        }) { Text("Confirmar encerramento") } }, dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancelar") } })
    closed?.let { result ->
        AlertDialog(onDismissRequest = {}, title = { Text("Conta encerrada") },
            text = { Text("${result.mensagem}\n\nProtocolo: ${result.protocolo}") },
            confirmButton = { TextButton(onClick = {
                context.startActivity(Intent(context, BemVindoActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }) { Text("Sair") } })
    }
}
