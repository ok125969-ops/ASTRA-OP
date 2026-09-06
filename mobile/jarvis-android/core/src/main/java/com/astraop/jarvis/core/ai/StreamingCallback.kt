package com.astraop.jarvis.core.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * Streaming callback helper — transforms a Flow of tokens into lifecycle events.
 */
interface StreamingCallback {
    fun onStart()
    fun onToken(token: String)
    fun onComplete()
    fun onError(throwable: Throwable)
}

fun Flow<String>.attachCallback(cb: StreamingCallback): Flow<String> =
    this.onStart { cb.onStart() }
        .onCompletion { cb.onComplete() }
