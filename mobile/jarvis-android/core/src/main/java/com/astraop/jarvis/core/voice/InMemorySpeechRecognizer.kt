package com.astraop.jarvis.core.voice

import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * In-memory SpeechRecognizer for unit tests — simulates partial results and a final result.
 */
class InMemorySpeechRecognizer(private val partials: List<String> = listOf(), private val final: String = "") : SpeechRecognizer {
    private var listener: SpeechListener? = null
    private var active = false

    override fun startListening(listener: SpeechListener) {
        this.listener = listener
        active = true
        GlobalScope.launch {
            for (p in partials) {
                if (!active) return@launch
                listener.onPartial(p)
                delay(40)
            }
            if (!active) return@launch
            listener.onResult(final)
        }
    }

    override fun stopListening() {
        active = false
    }

    override fun cancel() {
        active = false
    }
}
