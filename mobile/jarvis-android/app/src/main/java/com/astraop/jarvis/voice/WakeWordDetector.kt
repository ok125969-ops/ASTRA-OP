package com.astraop.jarvis.voice

/**
 * WakeWordDetector placeholder — experimental and OFF by default.
 * Implementations may use on-device models later.
 */
interface WakeWordDetector {
    fun start()
    fun stop()
    fun setWakePhrase(phrase: String)
}

class NoopWakeWordDetector : WakeWordDetector {
    private var phrase: String = "hey jarvis"
    override fun start() {}
    override fun stop() {}
    override fun setWakePhrase(phrase: String) { this.phrase = phrase }
}
