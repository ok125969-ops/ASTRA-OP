package com.astraop.jarvis.core.permissions

import android.app.Activity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

interface PermissionChecker {
    fun hasPermission(permission: String): Boolean
}

interface PermissionRequester {
    fun requestPermission(permission: String, callback: (Boolean) -> Unit)
}

// Lightweight AndroidPermissionManager wrapper (used in app module)
class AndroidPermissionManager(private val activity: Activity) : PermissionChecker, PermissionRequester {
    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        if (hasPermission(permission)) {
            callback(true)
            return
        }
        // App module should hook ActivityResultContracts; here we provide a placeholder.
        callback(false)
    }
}
