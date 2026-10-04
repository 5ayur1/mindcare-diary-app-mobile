package com.fiap.mindcarediary.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.KeyStore
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicLong
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface DraftStore {
    suspend fun load(): String?
    suspend fun save(value: String?)
}

/** Texto cifrado por AES-GCM; chave não exportável no Android Keystore; fora de backups. */
class ProtectedDraftStore(context: Context, account: String, kind: String, private val namespace: String = "drafts") : DraftStore {
    private val directory = File(context.applicationContext.noBackupFilesDir, namespace)
    private val name = MessageDigest.getInstance("SHA-256").digest("$account:$kind".toByteArray()).joinToString("") { "%02x".format(it) }
    private val file = AtomicFile(File(directory, name))
    private val generation = generations.computeIfAbsent(namespace) { AtomicLong() }
    private val session = generation.get()
    private val alias = aliasFor(namespace)
    init { require(account.isNotBlank() && account != "Unknown"); require(namespace.matches(Regex("[a-z-]+"))) }
    companion object {
        private val mutex = Mutex()
        private val generations = java.util.concurrent.ConcurrentHashMap<String, AtomicLong>()
        private fun aliasFor(namespace: String) = if (namespace == "drafts") ALIAS else "$ALIAS-$namespace"
        private const val ALIAS = "mindcare-drafts-v1"
        suspend fun clearAll(context: Context, namespace: String = "drafts") {
            require(namespace.matches(Regex("[a-z-]+")))
            val generation = generations.computeIfAbsent(namespace) { AtomicLong() }
            generation.incrementAndGet() // Escritas antigas não podem recriar rascunhos depois do logout.
            withContext(Dispatchers.IO) { mutex.withLock {
                KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(aliasFor(namespace)) }
                File(context.applicationContext.noBackupFilesDir, namespace).listFiles()?.forEach { check(it.delete()) }
            } }
        }
        private fun key(alias: String): SecretKey {
            val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            (store.getKey(alias, null) as? SecretKey)?.let { return it }
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
                init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
            }.generateKey()
        }
    }
    override suspend fun load(): String? = withContext(Dispatchers.IO) { mutex.withLock {
        if (session != generation.get() || !file.baseFile.exists()) return@withLock null
        require(file.baseFile.length() <= 262144)
        val bytes = file.openRead().use { it.readBytes() }
        require(bytes.size in 29..262144)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(alias), GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
        cipher.updateAAD(name.toByteArray())
        String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
    } }
    override suspend fun save(value: String?) { withContext(Dispatchers.IO) { mutex.withLock {
        if (session != generation.get()) return@withLock
        if (value == null) { file.delete(); return@withLock }
        val plain = value.toByteArray(Charsets.UTF_8)
        require(plain.size <= 262100)
        check(directory.exists() || directory.mkdirs())
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(alias)); cipher.updateAAD(name.toByteArray())
        val out = file.startWrite()
        try { out.write(cipher.iv); out.write(cipher.doFinal(plain)); file.finishWrite(out) }
        catch (e: Exception) { file.failWrite(out); throw e }
    } } }
}
