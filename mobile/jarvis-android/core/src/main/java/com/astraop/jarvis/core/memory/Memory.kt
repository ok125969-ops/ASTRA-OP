package com.astraop.jarvis.core.memory

/**
 * Memory interfaces for Phase 4 scaffolding. In-memory implementation provided for local use and tests.
 */
interface ConversationMemory {
    suspend fun addMessage(sessionId: String, message: MemoryMessage)
    suspend fun getHistory(sessionId: String, limit: Int = 50): List<MemoryMessage>
    suspend fun search(sessionId: String, query: String): List<MemoryMessage>
    suspend fun clear(sessionId: String)
}

data class MemoryMessage(
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long
)

class InMemoryConversationMemory : ConversationMemory {
    private val store = mutableMapOf<String, MutableList<MemoryMessage>>()

    override suspend fun addMessage(sessionId: String, message: MemoryMessage) {
        store.getOrPut(sessionId) { mutableListOf() }.add(message)
    }

    override suspend fun getHistory(sessionId: String, limit: Int): List<MemoryMessage> {
        return store[sessionId]?.takeLast(limit)?.toList() ?: emptyList()
    }

    override suspend fun search(sessionId: String, query: String): List<MemoryMessage> {
        return store[sessionId]?.filter { it.content.contains(query, ignoreCase = true) } ?: emptyList()
    }

    override suspend fun clear(sessionId: String) {
        store.remove(sessionId)
    }
}
