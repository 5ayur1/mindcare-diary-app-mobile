package com.fiap.mindcarediary

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.fiap.mindcarediary.paciente.HistoricoScreen
import com.fiap.mindcarediary.repository.HistoricoRepository
import com.fiap.mindcarediary.model.HistoricoPagina
import com.fiap.mindcarediary.service.RegistroDiario
import com.fiap.mindcarediary.viewmodel.HistoricoViewModel
import org.junit.Rule
import org.junit.Test
import java.io.File

class HistoricoScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun mostraHistoricoEPesquisaSemDadosReais() {
        val model = HistoricoViewModel(HistoricoRepository { filtro, pagina ->
            HistoricoPagina(if (filtro.texto == "inexistente") emptyList() else listOf(
                RegistroDiario("BOM", "Passeio com a família", "", "2026-10-04T10:00:00", 1L, null, "TRADITIONAL")
            ), pagina, false)
        })
        compose.setContent { MaterialTheme { HistoricoScreen(model) {} } }
        compose.waitUntil { model.state.value.registros.isNotEmpty() }
        compose.onNodeWithText("Meu histórico").assertIsDisplayed()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.cacheDir, "qa-historico.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        compose.onNodeWithText("Pesquisar no relato").performTextInput("inexistente")
        compose.onNodeWithText("Aplicar filtros").performClick()
        compose.onNodeWithText("Nenhum registro encontrado para os filtros escolhidos.").performScrollTo().assertIsDisplayed()
    }
}
