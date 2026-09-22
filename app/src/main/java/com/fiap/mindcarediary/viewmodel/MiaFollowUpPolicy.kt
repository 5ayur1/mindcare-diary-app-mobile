package com.fiap.mindcarediary.viewmodel

/** Only reviewed, normal registration questions may be replaced. Safety replies are untouched. */
object MiaFollowUpPolicy {
    private const val FEELING = "E como você se sentiu sobre isso?"
    private val followUps = listOf(
        FEELING,
        "Gostaria de contar um pouco mais sobre isso?",
        "Tem algo sobre o seu dia que você considera importante registrar?",
        "Existe mais alguma coisa que você gostaria de adicionar ao seu registro de hoje?"
    )
    private val opening = setOf("Como foi o seu dia hoje?", "Aconteceu algo hoje que você gostaria de registrar?")
    private const val OLD_FEELING = "Como você descreveria como está se sentindo agora?"

    fun reply(text: String, previousReplies: List<String>, fallback: Boolean): String {
        if (fallback || (text !in opening && text != OLD_FEELING && text !in followUps)) return text
        val used = previousReplies.map { if (it == OLD_FEELING) FEELING else it }.toSet()
        val preferred = if (text in opening || text == OLD_FEELING) FEELING else text
        return if (preferred !in used) preferred else followUps.firstOrNull { it !in used }
            ?: "Você pode continuar escrevendo ou revisar seu relato quando quiser."
    }
}
