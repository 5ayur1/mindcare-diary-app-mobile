package com.fiap.mindcarediary.model
import com.fiap.mindcarediary.service.RegistroDiario
data class HistoricoFiltro(val texto: String = "", val inicio: String = "", val fim: String = "", val humor: String? = null, val origem: String? = null)
data class HistoricoPagina(val registros: List<RegistroDiario>, val pagina: Int, val temMais: Boolean)
