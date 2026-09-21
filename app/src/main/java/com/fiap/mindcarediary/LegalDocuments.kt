package com.fiap.mindcarediary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

const val LEGAL_VERSION = "2026-09-21.1"

@Composable
fun LegalDocumentDialog(file: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val text = remember(file) { context.assets.open("legal/$file").bufferedReader().use { it.readText() } }
    AlertDialog(onDismissRequest = onClose,
        title = { Text(if (file == "termos.txt") "Termos de Uso" else "Política de Privacidade") },
        text = { Text(text, Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) },
        confirmButton = { TextButton(onClick = onClose) { Text("Fechar documento") } })
}

@Composable
fun LoginTermsDialog(onAccept: () -> Unit, onCancel: () -> Unit) {
    var document by remember { mutableStateOf<String?>(null) }
    var terms by remember { mutableStateOf(false) }
    var privacy by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onCancel,
        title = { Text("Antes de entrar") },
        text = {
            Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                Text("Leia os documentos do MindCare Diary. Esta confirmação será solicitada em toda tentativa de login.")
                TextButton(onClick = { document = "termos.txt" }) { Text("Ler Termos de Uso") }
                TextButton(onClick = { document = "privacidade.txt" }) { Text("Ler Política de Privacidade") }
                Row { Checkbox(checked = terms, onCheckedChange = { terms = it }); Text("Aceito os Termos de Uso.", Modifier.padding(top = 12.dp)) }
                Row { Checkbox(checked = privacy, onCheckedChange = { privacy = it }); Text("Estou ciente da Política de Privacidade.", Modifier.padding(top = 12.dp)) }
                Text("Esta confirmação não é autorização genérica para qualquer tratamento de dados de saúde.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(enabled = terms && privacy, onClick = onAccept) { Text("Aceitar e entrar") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancelar") } })
    document?.let { LegalDocumentDialog(it) { document = null } }
}
