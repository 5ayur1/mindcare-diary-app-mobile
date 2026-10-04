package com.fiap.mindcarediary.viewmodel

import com.fiap.mindcarediary.model.*
import com.fiap.mindcarediary.repository.*
import com.fiap.mindcarediary.service.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EvolucaoViewModelTest {
    private class MemoryDraft : DraftStore {
        var text: String? = null
        override suspend fun load() = text
        override suspend fun save(value: String?) { text = value }
    }
    @Test fun recuperaRascunhoEDescartaSemSalvarServidor() = runTest {
        val draft = MemoryDraft()
        val model = DiarioEditorViewModel({ _, _ -> error("Não deveria enviar") }, backgroundScope)
        model.bind("ana", draft); runCurrent()
        model.editar(positivo = "Passeio", negativo = "Cansaço", humor = "BOM"); runCurrent()
        val restored = DiarioEditorViewModel({ _, _ -> }, backgroundScope)
        restored.bind("ana", draft); runCurrent()
        assertEquals("Passeio", restored.state.value.positivo)
        assertEquals("Cansaço", restored.state.value.negativo)
        restored.descartar(); runCurrent()
        assertNull(draft.text)
    }
    @Test fun salvarNaoDuplicaEnquantoPendenteELimpaAposSucesso() = runTest {
        val result = CompletableDeferred<Unit>(); var calls = 0
        val draft = MemoryDraft()
        val model = DiarioEditorViewModel({ _, _ -> calls++; result.await() }, backgroundScope)
        model.bind("ana", draft); runCurrent(); model.editar(positivo = "Relato"); runCurrent()
        model.salvar(); model.salvar(); runCurrent()
        assertEquals(1, calls); assertNotNull(draft.text)
        result.complete(Unit); runCurrent()
        assertTrue(model.state.value.salvo); assertNull(draft.text)
    }
    @Test fun falhaDeEnvioPreservaRascunho() = runTest {
        val draft = MemoryDraft()
        val model = DiarioEditorViewModel({ _, _ -> throw java.io.IOException() }, backgroundScope)
        model.bind("ana", draft); runCurrent(); model.editar(positivo = "Relato"); runCurrent()
        model.salvar(); runCurrent()
        assertNotNull(model.state.value.erro); assertNotNull(draft.text); assertFalse(model.state.value.salvo)
    }
    @Test fun historicoValidaPeriodoEPreservaPaginaEmErro() = runTest {
        var fail = false; val pages = mutableListOf<Int>()
        val model = HistoricoViewModel(HistoricoRepository { _, page ->
            pages.add(page); if(fail) throw java.io.IOException()
            HistoricoPagina(listOf(RegistroDiario("BOM", "Relato", "", "2026-10-01", id = page.toLong())), page, page == 0)
        }, backgroundScope)
        model.buscar(HistoricoFiltro(inicio = "2026-10-05", fim = "2026-10-01")); runCurrent(); assertTrue(pages.isEmpty())
        model.buscar(HistoricoFiltro()); runCurrent(); fail = true
        model.mais(); runCurrent(); assertEquals(1, model.state.value.registros.size)
        fail = false; model.tentarNovamente(); runCurrent()
        assertEquals(listOf(0,1,1), pages); assertEquals(2, model.state.value.registros.size); assertFalse(model.state.value.temMais)
    }
    @Test fun chatRecuperaChaveDoSalvamentoIncertoSemGuardarRespostaDaMia() = runTest {
        val requests = mutableListOf<MiaRegistroRequest>()
        val repository = object : ChatRepository {
            override suspend fun send(message: String) = MiaMessageResponse("MIA", "Assistente", "RESPOSTA_NAO_PERSISTIR", false)
            override suspend fun save(request: MiaRegistroRequest): RegistroDiario { requests.add(request); throw java.io.IOException() }
        }
        val draft = MemoryDraft()
        val model = ChatViewModel(repository, backgroundScope)
        model.bindDraft(draft); runCurrent(); model.updateInput("Texto paciente"); model.send(); runCurrent()
        model.review(); model.save(); runCurrent()
        assertFalse(draft.text!!.contains("RESPOSTA_NAO_PERSISTIR"))
        val second = ChatViewModel(repository, backgroundScope)
        second.bindDraft(draft); runCurrent()
        assertTrue(second.state.value.reviewing); second.save(); runCurrent()
        assertEquals(2, requests.size); assertEquals(requests[0], requests[1])
        second.discardDraft(); runCurrent(); assertNull(draft.text)
    }
}
