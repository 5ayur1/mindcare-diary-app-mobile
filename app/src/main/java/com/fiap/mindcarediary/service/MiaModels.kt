package com.fiap.mindcarediary.service

data class MiaMessageRequest(val mensagem: String) {
    override fun toString() = "MiaMessageRequest[conteudo omitido]"
}

data class MiaMessageResponse(val assistente: String, val papel: String, val mensagem: String, val fallback: Boolean)

data class MiaRegistroRequest(val idRequisicao: String, val textoConfirmado: String, val nivelHumor: String) {
    override fun toString() = "MiaRegistroRequest[conteudo omitido]"
}
