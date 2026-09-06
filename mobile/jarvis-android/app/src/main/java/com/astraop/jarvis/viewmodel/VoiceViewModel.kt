package com.astraop.jarvis.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.astraop.jarvis.core.voice.VoiceSessionManager
import com.astraop.jarvis.core.voice.VoiceState
import com.astraop.jarvis.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceViewModel(private val context: Context) : ViewModel() {
    private val vsm: VoiceSessionManager = AppModule.provideVoiceSessionManager(context)

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    init {
        // Observe voice session manager state/transcript
        // For simplicity, poll the flows — in production use proper coroutine scope bindings
        // Here we just map initial state
    }

    fun startSession() {
        val sessionId = "session-${System.currentTimeMillis()}"
        _voiceState.tryEmit(VoiceState.LISTENING)
        vsm.startSession(sessionId)
        // In production we should collect vsm.state and vsm.transcript; for now, map simple flows
    }

    fun stopSession() {
        vsm.stopListening()
        _voiceState.tryEmit(VoiceState.PROCESSING)
    }

    fun cancel() {
        vsm.cancel()
        _voiceState.tryEmit(VoiceState.IDLE)
    }

    fun clearTranscript() { _transcript.tryEmit("") }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            // no-op here; UI should call startSession again
        }
    }

    fun hasRecordPermission(): Boolean {
        // The UI layer should check permissions via ContextCompat
        return true // placeholder — actual check occurs in composable via Permissions launcher
    }
}
