package com.fiap.mindcarediary

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fiap.mindcarediary.paciente.ChatMiaScreen
import com.fiap.mindcarediary.repository.ChatRepository
import com.fiap.mindcarediary.service.*
import com.fiap.mindcarediary.viewmodel.ChatViewModel
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Rule
import org.junit.Test

class ChatMiaScreenTest {
    @get:Rule val compose = createComposeRule()

    private val repository = object : ChatRepository {
        override suspend fun send(message: String) = MiaMessageResponse("MIA", "Assistente de registro", "Gostaria de contar mais?", false)
        override suspend fun save(request: MiaRegistroRequest) = Unit
    }

    @Test fun showsChatAndRequiresReviewBeforeSaving() {
        val model = ChatViewModel(repository)
        compose.setContent { MaterialTheme { ChatMiaScreen({}, {}, model) } }
        compose.onNodeWithText("MindCare Intelligent Assistent").assertIsDisplayed()
        File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "qa-chat.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        compose.onNodeWithContentDescription("Mensagem").performTextInput("Meu relato")
        compose.onNodeWithContentDescription("Enviar").performClick()
        compose.waitUntil { model.state.value.messages.size == 3 }
        compose.onNodeWithText("Revisar e salvar registro").performClick()
        compose.onNodeWithText("Texto do diário").assertTextContains("Meu relato")
        compose.onNodeWithText("Confirmar e salvar diário").performScrollTo().performClick()
        compose.onNodeWithText("Registro salvo no seu diário.").assertIsDisplayed()
    }

    @Test fun asksBeforeDiscardingDraft() {
        val model = ChatViewModel(repository)
        compose.setContent { MaterialTheme { ChatMiaScreen({}, {}, model) } }
        compose.onNodeWithContentDescription("Mensagem").performTextInput("Rascunho")
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Sair do chat?").assertIsDisplayed()
        compose.onNodeWithText("Continuar").performClick()
        compose.onNodeWithContentDescription("Mensagem").assertTextContains("Rascunho")
    }
}
