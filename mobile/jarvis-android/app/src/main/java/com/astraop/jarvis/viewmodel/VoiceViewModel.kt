package com.astraop.jarvis.viewmodel

import android.Manifest
import androidx.lifecycle.ViewModel
import com.astraop.jarvis.core.voice.VoiceSessionManager
import com.astraop.jarvis.core.voice.VoiceState
import com.astraop.jarvis.core.capability.Capability
import com.astraop.jarvis.core.capability.CapabilityManager
import com.astraop.jarvis.core.permissions.PermissionChecker
import com.astraop.jarvis.core.permissions.PermissionRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceViewModel(
    private val voiceSessionManager: VoiceSessionManager,
    private val permissionChecker: PermissionChecker,
    private val permissionRequester: PermissionRequester,
    private val capabilityManager: CapabilityManager
) : ViewModel() {

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    private val _showRationale = MutableStateFlow(false)
    val showRationale: StateFlow<Boolean> = _showRationale.asStateFlow()

    private val _openSettings = MutableStateFlow(false)
    val openSettings: StateFlow<Boolean> = _openSettings.asStateFlow()

    fun hasRecordPermission(): Boolean {
        return permissionChecker.hasPermission(Manifest.permission.RECORD_AUDIO)
    }

    fun startSession() {
        when (capabilityManager.check(Capability.VOICE)) {
            com.astraop.jarvis.core.capability.CapabilityState.AVAILABLE -> {
                val sessionId = "session-${System.currentTimeMillis()}"
                _voiceState.tryEmit(VoiceState.LISTENING)
                voiceSessionManager.startSession(sessionId)
            }
            com.astraop.jarvis.core.capability.CapabilityState.PERMISSION_REQUIRED -> {
                _showRationale.tryEmit(true)
            }
            else -> {
                _voiceState.tryEmit(VoiceState.ERROR)
            }
        }
    }

    fun requestRecordPermission() {
        permissionRequester.requestPermission(Manifest.permission.RECORD_AUDIO) { granted ->
            if (granted) {
                _showRationale.tryEmit(false)
            } else {
                // The caller (Activity) should detect whether the denial is permanent and call onPermissionDenied(true)
                _showRationale.tryEmit(true)
            }
        }
    }

    fun onPermissionDenied(permanently: Boolean) {
        if (permanently) {
            _openSettings.tryEmit(true)
        } else {
            _showRationale.tryEmit(true)
        }
    }

    fun stopSession() {
        voiceSessionManager.stopListening()
        _voiceState.tryEmit(VoiceState.PROCESSING)
    }

    fun cancel() {
        voiceSessionManager.cancel()
        _voiceState.tryEmit(VoiceState.IDLE)
    }
}
