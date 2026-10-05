package com.fiap.mindcarediary.paciente

import com.fiap.mindcarediary.model.*
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.viewmodel.*
import com.fiap.mindcarediary.ui.theme.MindcareDiaryTheme

class HistoricoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent { MindcareDiaryTheme(darkTheme = false, dynamicColor = false) { HistoricoScreen { finish() } } }
    }
}
@Composable
fun HistoricoScreen(model: HistoricoViewModel = viewModel(), onBack: () -> Unit) {
    val state by model.state.collectAsStateWithLifecycle()
    var filtro by remember { mutableStateOf(HistoricoFiltro()) }
    LaunchedEffect(Unit) { model.buscar() }
    Scaffold { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                TextButton(onClick = onBack) { Text("Voltar") }
                Text("Meu histórico", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(filtro.texto, { if(it.length <= 200) filtro = filtro.copy(texto = it) }, label = { Text("Pesquisar no relato") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(filtro.inicio, { filtro = filtro.copy(inicio = it) }, label = { Text("De: AAAA-MM-DD") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(filtro.fim, { filtro = filtro.copy(fim = it) }, label = { Text("Até: AAAA-MM-DD") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                FiltroMenu("Origem", filtro.origem, listOf("TRADITIONAL" to "Diário tradicional", "CHAT" to "Chat com a MIA")) { filtro = filtro.copy(origem = it) }
                FiltroMenu("Humor", filtro.humor, listOf("OTIMO" to "Ótimo", "BOM" to "Bom", "NEUTRO" to "Neutro", "MAL" to "Mal", "PESSIMO" to "Péssimo", "SEM_DEFINICAO" to "Não informado")) { filtro = filtro.copy(humor = it) }
                Button(onClick = { model.buscar(filtro) }, enabled = !state.carregando) { Text("Aplicar filtros") }
            }
            items(state.registros, key = { it.id ?: it.dataHoraCriacao }) { registro ->
                DiaryCard(DiaryItem(converteParaEmoji(registro.nivelHumor ?: "SEM_DEFINICAO"), registro.dataHoraCriacao.substringBefore("T"),
                    registro.textoConfirmado ?: listOfNotNull(registro.pontosPositivos, registro.dificuldadesDesafios).filter { it.isNotBlank() }.joinToString("\n\n"), registro.origem ?: "TRADITIONAL"))
            }
            item {
                if (state.carregando) LinearProgressIndicator(Modifier.fillMaxWidth())
                else if (state.erro != null) {
                    Text(state.erro!!, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = model::tentarNovamente) { Text("Tentar novamente") }
                } else if (state.registros.isEmpty()) Text("Nenhum registro encontrado para os filtros escolhidos.")
                else if (state.temMais) TextButton(onClick = model::mais) { Text("Carregar mais") }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
@Composable
private fun FiltroMenu(label: String, selected: String?, options: List<Pair<String, String>>, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) { Text("$label: ${options.find { it.first == selected }?.second ?: "Todos"}") }
        DropdownMenu(expanded, { expanded = false }) {
            DropdownMenuItem(text = { Text("Todos") }, onClick = { onSelect(null); expanded = false })
            options.forEach { (value, title) -> DropdownMenuItem(text = { Text(title) }, onClick = { onSelect(value); expanded = false }) }
        }
    }
}
