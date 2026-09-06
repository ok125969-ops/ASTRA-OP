package com.astraop.jarvis.core.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Job

/**
 * Provider-independent AI interface.
 */
interface AIProvider {
    suspend fun chat(messages: List<AIMessage>, config: ChatConfig = ChatConfig()): AIResponse

    /**
     * Streaming chat returns a Flow of token strings. The consumer should collect
     * until completion. The returned Job represents the background work and may be cancelled.
     */
    fun streamChat(messages: List<AIMessage>, config: ChatConfig = ChatConfig()): Flow<String>

    suspend fun embedding(text: String): FloatArray
}

data class AIMessage(val role: String, val content: String)

data class ChatConfig(val maxTokens: Int = 1024)

data class AIResponse(val content: String, val reasoning: String? = null)
