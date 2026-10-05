package com.fiap.mindcarediary.repository
import com.fiap.mindcarediary.model.*
import com.fiap.mindcarediary.service.RetrofitClient
fun interface HistoricoRepository { suspend fun buscar(filtro: HistoricoFiltro, pagina: Int): HistoricoPagina }
class ApiHistoricoRepository : HistoricoRepository {
    override suspend fun buscar(filtro: HistoricoFiltro, pagina: Int) = RetrofitClient.api.historico(
        filtro.texto.takeIf { it.isNotBlank() }, filtro.inicio.takeIf { it.isNotBlank() }, filtro.fim.takeIf { it.isNotBlank() }, filtro.humor, filtro.origem, pagina)
}
