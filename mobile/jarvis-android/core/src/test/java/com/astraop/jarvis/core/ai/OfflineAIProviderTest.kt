package com.astraop.jarvis.core.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue

class OfflineAIProviderTest {
    @Test
    fun testChatEcho() = runTest {
        val provider = OfflineAIProvider()
        val resp = provider.chat(listOf(AIMessage("user", "hello")))
        assertTrue(resp.content.contains("hello"))
    }
}
