package com.astraop.jarvis.core.ai

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class ConversationManagerTest {
    @Test
    fun testStreamingFlow() = runTest {
        val provider = OfflineAIProvider()
        val conv = ConversationManager(provider)
        val flow = conv.sendMessage("s1", listOf(AIMessage("user", "what's up")))
        val tokens = flow.toList()
        // Expect tokens split by words from the last message
        assertEquals(true, tokens.isNotEmpty())
    }
}
