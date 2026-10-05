package com.fiap.mindcarediary
import androidx.test.platform.app.InstrumentationRegistry
import com.fiap.mindcarediary.repository.ProtectedDraftStore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ProtectedDraftStoreTest {
    @Test fun cipherIsolatesAccountsAndLogoutInvalidatesOldWriters() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ProtectedDraftStore.clearAll(context, "draft-tests")
        val a = ProtectedDraftStore(context, "teste-a", "diario", "draft-tests")
        val b = ProtectedDraftStore(context, "teste-b", "diario", "draft-tests")
        try {
            a.save("RELATO_SECRETO")
            assertEquals("RELATO_SECRETO", a.load()); assertNull(b.load())
            File(context.noBackupFilesDir, "draft-tests").listFiles()!!.forEach { assertFalse(String(it.readBytes()).contains("RELATO_SECRETO")) }
            ProtectedDraftStore.clearAll(context, "draft-tests")
            a.save("ESCRITA_ATRASADA")
            assertNull(ProtectedDraftStore(context, "teste-a", "diario", "draft-tests").load())
        } finally { ProtectedDraftStore.clearAll(context, "draft-tests") }
    }
}
