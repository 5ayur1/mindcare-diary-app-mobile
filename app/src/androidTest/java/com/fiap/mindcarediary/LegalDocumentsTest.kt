package com.fiap.mindcarediary

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LegalDocumentsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun aceiteRequerDuasMarcacoesEReapareceDesmarcado() {
        var visible by mutableStateOf(true)
        var logins = 0
        compose.setContent {
            if (visible) LoginTermsDialog(onAccept = { logins++; visible = false }, onCancel = { visible = false })
        }
        compose.onNodeWithText("Aceitar e entrar").assertIsNotEnabled()
        compose.onAllNodes(isToggleable())[0].performClick()
        compose.onNodeWithText("Aceitar e entrar").assertIsNotEnabled()
        compose.onAllNodes(isToggleable())[1].performClick()
        compose.onNodeWithText("Aceitar e entrar").performClick()
        compose.runOnIdle { assertEquals(1, logins); visible = true }
        compose.onNodeWithText("Aceitar e entrar").assertIsNotEnabled()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(1, logins) }
    }
}
