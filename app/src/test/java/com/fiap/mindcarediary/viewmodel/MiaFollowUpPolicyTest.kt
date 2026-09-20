package com.fiap.mindcarediary.viewmodel

import org.junit.Assert.*
import org.junit.Test

class MiaFollowUpPolicyTest {
    @Test fun neverRepeatsOpeningAndAsksAboutFeelings() {
        for (question in listOf("Como foi o seu dia hoje?", "Aconteceu algo hoje que você gostaria de registrar?")) {
            assertEquals("E como você se sentiu sobre isso?", MiaFollowUpPolicy.reply(question, listOf("Como foi o seu dia hoje?"), false))
        }
    }
    @Test fun doesNotRepeatQuestionsAndEventuallyAllowsClosing() {
        val previous = mutableListOf("Como foi o seu dia hoje?")
        repeat(4) {
            val reply = MiaFollowUpPolicy.reply("Como foi o seu dia hoje?", previous, false)
            assertFalse(reply in previous); previous += reply
        }
        assertEquals("Você pode continuar escrevendo ou revisar seu relato quando quiser.",
            MiaFollowUpPolicy.reply("Como foi o seu dia hoje?", previous, false))
    }
    @Test fun preservesSafetyRepliesAndFallbacksVerbatim() {
        val safety = "Não posso recomendar medicamentos ou tratamentos."
        assertEquals(safety, MiaFollowUpPolicy.reply(safety, listOf(safety), false))
        assertEquals("Como foi o seu dia hoje?", MiaFollowUpPolicy.reply("Como foi o seu dia hoje?", emptyList(), true))
    }
}
