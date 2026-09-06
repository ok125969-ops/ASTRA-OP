package com.astraop.jarvis.core.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Simple planner that can detect tool calls in assistant responses.
 * This is a lightweight placeholder for a more advanced planner.
 */
object SimplePlanner {
    fun extractToolCall(text: String): Pair<String, Map<String, Any>>? {
        // Very naive: if text contains [tool:NAME|k=v], parse it.
        val regex = "\\[tool:(\\w+)(?:\|(.*))?]".toRegex()
        val match = regex.find(text) ?: return null
        val name = match.groupValues[1]
        val argsRaw = match.groupValues.getOrNull(2) ?: ""
        val args = argsRaw.split(',').mapNotNull { part ->
            val kv = part.split('=')
            if (kv.size == 2) kv[0].trim() to kv[1].trim() else null
        }.toMap()
        return name to args
    }
}
