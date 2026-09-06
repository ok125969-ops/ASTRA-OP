package com.astraop.jarvis.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.astraop.jarvis.core.voice.TextToSpeechEngine
import com.astraop.jarvis.core.voice.VoiceConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class AndroidTextToSpeech(private val context: Context) : TextToSpeechEngine {
    private val tts: TextToSpeech = TextToSpeech(context) { status ->
        // noop
    }

    init {
        // default locale; apps may set voice and rate via provided API
        tts.language = Locale.getDefault()
    }

    override suspend fun speak(text: String, voiceConfig: VoiceConfig) = suspendCancellableCoroutine<Unit> { cont ->
        val utteranceId = System.currentTimeMillis().toString()
        tts.setSpeechRate(voiceConfig.speechRate)
        tts.setPitch(voiceConfig.pitch)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { if (!cont.isCompleted) cont.resume(Unit) }
            override fun onError(utteranceId: String?) { if (!cont.isCompleted) cont.resume(Unit) }
        })
        val params = Bundle()
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, utteranceId)
        cont.invokeOnCancellation { /* stop speaking if coroutine cancelled */
            try { tts.stop() } catch (_: Exception) {}
        }
    }

    override fun stop() {
        try { tts.stop() } catch (_: Exception) {}
    }
}
