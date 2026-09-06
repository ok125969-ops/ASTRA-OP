package com.astraop.jarvis.core.voice

import kotlinx.coroutines.delay

/**
 * In-memory TTS for unit tests — captures speech and simulates duration.
 */
class InMemoryTextToSpeech : TextToSpeechEngine {
    val spoken = StringBuilder()

    override suspend fun speak(text: String, voiceConfig: VoiceConfig) {
        spoken.append(text)
        // Simulate speaking time proportional to text length
        val words = text.split(Regex("\\s+"))
        delay((words.size * 20).toLong())
    }

    override fun stop() {
        // No-op for in-memory
    }
}
