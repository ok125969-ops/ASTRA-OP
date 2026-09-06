package com.astraop.jarvis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import com.astraop.jarvis.di.AppModule
import com.astraop.jarvis.permissions.AppPermissionRequester
import com.astraop.jarvis.permissions.AppPermissionChecker
import com.astraop.jarvis.ui.VoiceInputScreen
import com.astraop.jarvis.viewmodel.VoiceViewModel

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionRequester: AppPermissionRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the permission launcher first. The requester will be initialized after but the callback won't fire until user acts.
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // deliver to the requester
            if (this::permissionRequester.isInitialized) {
                permissionRequester.onResult(granted)
            }
        }

        permissionRequester = AppPermissionRequester { permissionLauncher }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                // Provision dependencies manually for now. In production use Hilt/Dagger.
                val permissionChecker = AppModule.providePermissionChecker(this)
                val capabilityManager = AppModule.provideCapabilityManager(this, permissionChecker)
                val vsm = AppModule.provideVoiceSessionManager(this)
                val viewModel = VoiceViewModel(vsm, permissionChecker, permissionRequester, capabilityManager)

                VoiceInputScreen(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If returning from settings, the UI should re-check permission state; VoiceViewModel observes capabilityManager when starting sessions.
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}
