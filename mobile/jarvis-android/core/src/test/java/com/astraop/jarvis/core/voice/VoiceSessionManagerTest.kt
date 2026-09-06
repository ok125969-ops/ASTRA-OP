package com.astraop.jarvis.core.voice

import com.astraop.jarvis.core.ai.OfflineAIProvider
import com.astraop.jarvis.core.ai.ConversationManager
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertTrue

class VoiceSessionManagerTest {
    @Test
    fun testVoiceSessionFlows() = runTest {
        val provider = OfflineAIProvider("JARVIS")
        val conv = ConversationManager(provider)
        val recognizer = InMemorySpeechRecognizer(listOf("hello"), "hello world")
        val tts = InMemoryTextToSpeech()
        val vsm = VoiceSessionManager(recognizer, tts, conv)

        vsm.startSession("session-1")

        // Allow the in-memory recognizer and provider to run
        kotlinx.coroutines.delay(500)

        // Check that TTS captured a response (OfflineAIProvider echoes input)
        assertTrue(tts.spoken.isNotEmpty())
    }
}
