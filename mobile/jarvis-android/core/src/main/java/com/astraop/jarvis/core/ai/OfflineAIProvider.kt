package com.astraop.jarvis.core.ai

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Offline provider that streams back a canned response token-by-token.
 * Useful for unit tests and as a fallback.
 */
class OfflineAIProvider(private val persona: String = "JARVIS") : AIProvider {
    override suspend fun chat(messages: List<AIMessage>, config: ChatConfig): AIResponse {
        // Very simple echo-like behavior
        val last = messages.lastOrNull()?.content ?: ""
        val content = "${persona}: I heard you say: '$last'"
        return AIResponse(content)
    }

    override fun streamChat(messages: List<AIMessage>, config: ChatConfig): Flow<String> = flow {
        val response = (messages.lastOrNull()?.content ?: "").split(Regex("\\s+"))
        for (token in response) {
            emit(token + " ")
            delay(50)
        }
    }

    override suspend fun embedding(text: String): FloatArray {
        // Fake deterministic embedding for tests
        return FloatArray(8) { (text.hashCode() % 100 / 100f) }
    }
}
