package com.fiap.mindcarediary.paciente

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/** Foreground-only recognition. Audio is managed by Android; no audio file is saved. */
class HoldSpeechController(
    private val context: Context,
    private val onFinish: (String?, String?) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var recognizer: SpeechRecognizer? = null
    private var active = false
    private var held = false
    private var generation = 0
    private val segments = mutableListOf<String>()
    private var partial = ""

    fun start() {
        if (active) return
        active = true; held = true; segments.clear(); partial = ""
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            finish("Reconhecimento de voz indisponível neste aparelho. Você pode digitar.")
            return
        }
        listen()
    }

    private fun listen() {
        if (!active || !held) return
        val session = ++generation
        partial = ""
        try {
            val speech = SpeechRecognizer.createSpeechRecognizer(context)
            recognizer = speech
            speech.setRecognitionListener(object : RecognitionListener {
                private fun current() = active && generation == session
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onPartialResults(results: Bundle?) {
                    if (current()) partial = text(results)
                }
                override fun onResults(results: Bundle?) {
                    if (!current()) return
                    val finalText = text(results)
                    if (finalText.isNotBlank()) segments += finalText
                    partial = ""
                    nextOrFinish()
                }
                override fun onError(error: Int) {
                    if (!current()) return
                    if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        if (partial.isNotBlank()) segments += partial
                        partial = ""
                        nextOrFinish()
                    } else {
                        finish(when (error) {
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permita o microfone nas configurações do aplicativo."
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Falha de conexão do serviço de voz. Revise o trecho recuperado ou tente novamente."
                            else -> "O serviço de voz foi interrompido. Revise o trecho recuperado ou tente novamente."
                        })
                    }
                }
            })
            speech.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // Hints only: some native services ignore these silence durations.
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
                if (Build.VERSION.SDK_INT >= 33) {
                    putExtra(RecognizerIntent.EXTRA_ENABLE_FORMATTING, RecognizerIntent.FORMATTING_OPTIMIZE_QUALITY)
                }
            })
        } catch (_: SecurityException) {
            finish("Permita o microfone nas configurações do aplicativo.")
        } catch (_: RuntimeException) {
            finish("Não foi possível iniciar o serviço de voz. Você pode digitar.")
        }
    }

    private fun text(results: Bundle?) = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        ?.firstOrNull { it.isNotBlank() }?.trim().orEmpty()

    private fun nextOrFinish() {
        disposeRecognizer()
        if (segments.sumOf { it.length + 1 } > 4000) {
            finish("O ditado atingiu o limite do campo. Revise antes de continuar.")
        } else if (held) {
            handler.postDelayed({ listen() }, 200)
        } else finish()
    }

    fun release() {
        if (!active || !held) return
        held = false
        handler.removeCallbacksAndMessages(null)
        if (recognizer == null) finish()
        else {
            try { recognizer?.stopListening() } catch (_: RuntimeException) { finish(); return }
            handler.postDelayed({ if (active) finish("A transcrição foi interrompida. Confira o texto recuperado.") }, 5000)
        }
    }

    fun interrupt() {
        if (active) finish("Ditado interrompido ao sair da tela. Confira o texto recuperado.")
    }

    private fun finish(error: String? = null) {
        if (!active) return
        active = false; held = false
        handler.removeCallbacksAndMessages(null)
        if (partial.isNotBlank()) segments += partial
        val result = segments.joinToString(" ")
        segments.clear(); partial = ""
        disposeRecognizer()
        onFinish(result.ifBlank { null }, error ?: if (result.isBlank()) "Nenhuma fala reconhecida. Segure o botão e tente novamente." else null)
    }

    private fun disposeRecognizer() {
        generation++
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
    }

    fun close() {
        interrupt()
        handler.removeCallbacksAndMessages(null)
        disposeRecognizer()
    }
}
