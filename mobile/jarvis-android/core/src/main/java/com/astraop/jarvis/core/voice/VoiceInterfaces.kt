package com.astraop.jarvis.core.voice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Voice subsystem interfaces for the core runtime. Implementations for Android should live in the app module.
 * These interfaces are intentionally platform-agnostic so core unit tests may run on the JVM.
 */

enum class VoiceState { IDLE, LISTENING, PROCESSING, SPEAKING, ERROR }

interface SpeechListener {
    fun onAudioLevel(level: Float)
    fun onPartial(text: String)
    fun onResult(text: String)
    fun onError(error: Throwable)
}

interface SpeechRecognizer {
    fun startListening(listener: SpeechListener)
    fun stopListening()
    fun cancel()
}

interface TextToSpeechEngine {
    suspend fun speak(text: String, voiceConfig: VoiceConfig = VoiceConfig())
    fun stop()
}

interface WakeWordDetector {
    fun start()
    fun stop()
    fun setWakePhrase(phrase: String)
}

data class VoiceConfig(val language: String = "en-US", val speechRate: Float = 1f, val pitch: Float = 1f)

/**
 * VoiceSessionManager orchestrates a voice interaction: listen -> transcribe -> send to runtime -> play response.
 * It deliberately uses ConversationManager (central runtime) to process transcribed text instead of calling AI provider directly.
 */
class VoiceSessionManager(
    private val recognizer: SpeechRecognizer,
    private val tts: TextToSpeechEngine,
    private val conversationManager: com.astraop.jarvis.core.ai.ConversationManager,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _state = MutableStateFlow(VoiceState.IDLE)
    val state: StateFlow<VoiceState> = _state

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript

    private var currentSessionId: String? = null

    fun startSession(sessionId: String) {
        if (_state.value == VoiceState.LISTENING) return
        currentSessionId = sessionId
        _state.value = VoiceState.LISTENING

        recognizer.startListening(object : SpeechListener {
            override fun onAudioLevel(level: Float) {
                // Could update UI audio level flows
            }

            override fun onPartial(text: String) {
                _transcript.value = text
            }

            override fun onResult(text: String) {
                _transcript.value = text
                _state.value = VoiceState.PROCESSING
                handleFinalTranscript(sessionId, text)
            }

            override fun onError(error: Throwable) {
                _state.value = VoiceState.ERROR
                recognizer.cancel()
            }
        })
    }

    fun stopListening() {
        recognizer.stopListening()
        if (_state.value == VoiceState.LISTENING) {
            _state.value = VoiceState.PROCESSING
        }
    }

    private fun handleFinalTranscript(sessionId: String, text: String) {
        scope.launch {
            // Route through ConversationManager: create a message sequence with context handled externally
            val messages = listOf(com.astraop.jarvis.core.ai.AIMessage("user", text))

            // Stream tokens from conversation manager (provider) and accumulate response
            val tokenFlow = conversationManager.sendMessage(sessionId, messages)
            val sb = StringBuilder()
            tokenFlow.collect { token ->
                sb.append(token)
                // UI can display streaming result by exposing another Flow if desired
            }

            val final = sb.toString().trim()

            // Speak the final response via TTS
            _state.value = VoiceState.SPEAKING
            try {
                withContext(Dispatchers.Default) {
                    tts.speak(final)
                }
            } catch (e: Throwable) {
                _state.value = VoiceState.ERROR
            } finally {
                _state.value = VoiceState.IDLE
            }
        }
    }

    fun cancel() {
        recognizer.cancel()
        tts.stop()
        scope.cancel()
        _state.value = VoiceState.IDLE
    }
}
