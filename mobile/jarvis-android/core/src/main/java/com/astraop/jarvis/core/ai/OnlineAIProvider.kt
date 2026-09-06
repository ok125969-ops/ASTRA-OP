package com.astraop.jarvis.core.ai

/**
 * OnlineAIProvider scaffold — no keys, no network calls here. Implementations should be provided in app/remote modules.
 */
interface OnlineAIProvider : AIProvider {
    // marker interface; concrete implementations should accept configuration (API host, model, key via secure runtime)
}

// Example placeholder implementation that throws if used — forces callers to prefer OfflineAIProvider unless configured
class OnlineAIProviderPlaceholder : OnlineAIProvider {
    override suspend fun chat(messages: List<AIMessage>, config: ChatConfig): AIResponse {
        throw UnsupportedOperationException("OnlineAIProvider is not configured. Provide a concrete implementation in app module.")
    }

    override fun streamChat(messages: List<AIMessage>, config: ChatConfig): kotlinx.coroutines.flow.Flow<String> =
        kotlinx.coroutines.flow.flow { throw UnsupportedOperationException("OnlineAIProvider not configured") }

    override suspend fun embedding(text: String): FloatArray {
        throw UnsupportedOperationException("OnlineAIProvider not configured")
    }
}
