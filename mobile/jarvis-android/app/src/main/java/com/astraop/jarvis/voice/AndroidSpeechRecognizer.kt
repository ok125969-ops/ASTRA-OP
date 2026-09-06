package com.astraop.jarvis.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer as AndroidSR
import com.astraop.jarvis.core.voice.SpeechListener
import com.astraop.jarvis.core.voice.SpeechRecognizer

/**
 * AndroidSpeechRecognizer: adapts Android SpeechRecognizer to core SpeechRecognizer interface.
 * - provides partial results when available
 * - provides final transcript
 * - handles errors and cancellation
 */
class AndroidSpeechRecognizer(private val context: Context) : SpeechRecognizer {
    private var sr: AndroidSR? = null
    private var active = false

    override fun startListening(listener: SpeechListener) {
        if (active) return
        if (sr == null) sr = AndroidSR.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {
                    listener.onAudioLevel(rmsdB)
                }

                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(AndroidSR.RESULTS_RECOGNITION)
                    val text = matches?.joinToString(" ") ?: ""
                    if (text.isNotBlank()) listener.onPartial(text)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(AndroidSR.RESULTS_RECOGNITION)
                    val text = matches?.joinToString(" ") ?: ""
                    listener.onResult(text)
                    active = false
                }

                override fun onError(error: Int) {
                    listener.onError(RuntimeException("SpeechRecognizer error: $error"))
                    active = false
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }

        try {
            sr?.startListening(intent)
            active = true
        } catch (e: Exception) {
            listener.onError(e)
        }
    }

    override fun stopListening() {
        try {
            sr?.stopListening()
        } catch (_: Exception) {}
        active = false
    }

    override fun cancel() {
        try {
            sr?.cancel()
            sr?.destroy()
        } catch (_: Exception) {}
        sr = null
        active = false
    }
}
