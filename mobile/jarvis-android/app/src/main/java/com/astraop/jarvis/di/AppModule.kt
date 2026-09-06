package com.astraop.jarvis.di

import android.content.Context
import com.astraop.jarvis.core.ai.ConversationManager
import com.astraop.jarvis.core.ai.OfflineAIProvider
import com.astraop.jarvis.core.voice.VoiceSessionManager
import com.astraop.jarvis.voice.AndroidSpeechRecognizer
import com.astraop.jarvis.voice.AndroidTextToSpeech
import com.astraop.jarvis.voice.NoopWakeWordDetector

/**
 * Very small, manual DI to wire Android adapters into the core runtime.
 * In production this would be replaced by Hilt/Dagger with proper scopes.
 */
object AppModule {
    fun provideConversationManager(): ConversationManager {
        val provider = OfflineAIProvider("JARVIS")
        return ConversationManager(provider)
    }

    fun provideVoiceSessionManager(context: Context): VoiceSessionManager {
        val recognizer = AndroidSpeechRecognizer(context)
        val tts = AndroidTextToSpeech(context)
        val conv = provideConversationManager()
        val vsm = VoiceSessionManager(recognizer, tts, conv)
        // Wake word kept external; default noop
        return vsm
    }

    fun provideWakeWordDetector(): NoopWakeWordDetector = NoopWakeWordDetector()
}
