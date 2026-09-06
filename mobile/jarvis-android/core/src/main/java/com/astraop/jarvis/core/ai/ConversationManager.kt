package com.astraop.jarvis.core.ai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Simple in-memory context manager that tracks session histories.
 */
class ContextManager {
    private val sessions: MutableMap<String, MutableList<AIMessage>> = mutableMapOf()

    fun createSession(sessionId: String) {
        sessions.putIfAbsent(sessionId, mutableListOf())
    }

    fun addMessage(sessionId: String, message: AIMessage) {
        sessions.getOrPut(sessionId) { mutableListOf() }.add(message)
    }

    fun getRecent(sessionId: String, limit: Int = 20): List<AIMessage> {
        return sessions[sessionId]?.takeLast(limit)?.toList() ?: emptyList()
    }

    fun clearSession(sessionId: String) {
        sessions.remove(sessionId)
    }
}

/**
 * Conversation manager handles sending user messages to providers, streaming, cancellation and retries.
 */
class ConversationManager(private val provider: AIProvider) {
    private val scope = CoroutineScope(Dispatchers.Default)

    data class SessionHandle(val sessionId: String)

    fun startSession(sessionId: String): SessionHandle {
        return SessionHandle(sessionId).also { /* context setup if needed */ }
    }

    fun sendMessage(sessionId: String, messages: List<AIMessage>): Flow<String> {
        // Expose a token stream to the UI
        return provider.streamChat(messages)
    }
}
