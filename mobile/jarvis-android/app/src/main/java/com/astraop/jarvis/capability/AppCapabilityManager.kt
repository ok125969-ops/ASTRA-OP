package com.astraop.jarvis.capability

import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.content.ContextCompat
import com.astraop.jarvis.core.capability.Capability
import com.astraop.jarvis.core.capability.CapabilityManager
import com.astraop.jarvis.core.capability.CapabilityState
import com.astraop.jarvis.core.permissions.PermissionChecker

/**
 * App-side capability manager that inspects device features and permission state.
 * Uses official Android APIs to determine support and permissions.
 */
class AppCapabilityManager(private val context: Context, private val permissionChecker: PermissionChecker) : CapabilityManager {
    override fun check(capability: Capability): CapabilityState {
        return when (capability) {
            Capability.VOICE -> {
                val hasMic = context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
                if (!hasMic) return CapabilityState.NOT_SUPPORTED
                val granted = permissionChecker.hasPermission(Manifest.permission.RECORD_AUDIO)
                if (!granted) return CapabilityState.PERMISSION_REQUIRED
                CapabilityState.AVAILABLE
            }
            Capability.CAMERA -> {
                val hasCam = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                if (!hasCam) return CapabilityState.NOT_SUPPORTED
                val granted = permissionChecker.hasPermission(Manifest.permission.CAMERA)
                if (!granted) return CapabilityState.PERMISSION_REQUIRED
                CapabilityState.AVAILABLE
            }
            Capability.NOTIFICATIONS -> {
                // Notifications availability varies by API and user settings; mark AVAILABLE here.
                CapabilityState.AVAILABLE
            }
            Capability.APPS -> {
                // Basic app-launching support is generally available on Android.
                CapabilityState.AVAILABLE
            }
            Capability.FILES -> {
                // Files access depends on storage permissions (scoped storage on modern Android). We leave it PERMISSION_REQUIRED and let callers refine.
                CapabilityState.PERMISSION_REQUIRED
            }
            Capability.MEDIA -> CapabilityState.AVAILABLE
            Capability.WEB -> CapabilityState.AVAILABLE
            Capability.ASSISTANT_ROLE -> {
                // Assistant role support detection requires querying RoleManager (API 29+). We'll conservatively report NOT_SUPPORTED here and let app detect explicitly if supported.
                CapabilityState.NOT_SUPPORTED
            }
            else -> CapabilityState.NOT_SUPPORTED
        }
    }
}
