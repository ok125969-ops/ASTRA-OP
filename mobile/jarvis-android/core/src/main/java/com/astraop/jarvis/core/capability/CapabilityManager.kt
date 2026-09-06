package com.astraop.jarvis.core.capability

/**
 * Capability manager for device capability detection and reporting.
 * Core module implementation is limited and returns NOT_SUPPORTED for Android-specific capabilities.
 */

enum class CapabilityState { AVAILABLE, UNAVAILABLE, PERMISSION_REQUIRED, DEVICE_RESTRICTED, NOT_SUPPORTED }

enum class Capability { VOICE, CAMERA, NOTIFICATIONS, APPS, FILES, MEDIA, WEB, ASSISTANT_ROLE }

interface CapabilityManager {
    fun check(capability: Capability): CapabilityState
}

/**
 * Basic capability manager used in core unit tests. Android app module provides a platform-aware implementation.
 */
class BasicCapabilityManager : CapabilityManager {
    override fun check(capability: Capability): CapabilityState {
        // Core library is platform-agnostic: report NOT_SUPPORTED to force app module to provide a proper implementation.
        return CapabilityState.NOT_SUPPORTED
    }
}
