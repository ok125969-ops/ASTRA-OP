package com.astraop.jarvis.permissions

import android.content.Context
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.astraop.jarvis.core.permissions.PermissionChecker

/**
 * App-side PermissionChecker implementation using Android Context.
 */
class AppPermissionChecker(private val context: Context) : PermissionChecker {
    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
