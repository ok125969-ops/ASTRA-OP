package com.astraop.jarvis.permissions

import androidx.activity.result.ActivityResultLauncher
import com.astraop.jarvis.core.permissions.PermissionRequester

/**
 * App-side PermissionRequester that delegates to an ActivityResultLauncher created in the Activity/Composable.
 *
 * Usage pattern (Activity/Composable):
 *  - create an ActivityResultLauncher<String> via registerForActivityResult(RequestPermission()) { granted -> appPermissionRequester.onResult(granted) }
 *  - pass a lambda to AppPermissionRequester that returns that launcher: { myLauncher }
 *  - call requestPermission(permission, callback)
 */
class AppPermissionRequester(private val launcherProvider: () -> ActivityResultLauncher<String>) : PermissionRequester {
    private var pendingCallback: ((Boolean) -> Unit)? = null

    override fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        pendingCallback = callback
        try {
            launcherProvider().launch(permission)
        } catch (t: Throwable) {
            // If launcher not available or error, report failure
            val cb = pendingCallback
            pendingCallback = null
            cb?.invoke(false)
        }
    }

    /**
     * Should be called by the ActivityResultLauncher callback to deliver the result.
     */
    fun onResult(granted: Boolean) {
        val cb = pendingCallback
        pendingCallback = null
        cb?.invoke(granted)
    }
}
