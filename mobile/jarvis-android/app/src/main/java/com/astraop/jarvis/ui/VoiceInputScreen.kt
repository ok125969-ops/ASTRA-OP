package com.astraop.jarvis.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astraop.jarvis.core.voice.VoiceState
import com.astraop.jarvis.viewmodel.VoiceViewModel

@Composable
fun VoiceInputScreen(viewModel: VoiceViewModel) {
    val state by viewModel.voiceState.collectAsState()
    val transcript by viewModel.transcript.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.onPermissionResult(granted)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("JARVIS Voice", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier
                .size(160.dp)
                .background(if (state == VoiceState.LISTENING) Color.Green else Color.DarkGray)
                .clickable {
                    // Request permission if needed
                    if (!viewModel.hasRecordPermission()) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        if (state == VoiceState.IDLE) viewModel.startSession() else viewModel.stopSession()
                    }
                }, contentAlignment = Alignment.Center) {
                Text(if (state == VoiceState.LISTENING) "Listening" else "Tap to Speak", color = Color.White, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("State: ${state.name}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Transcript:")
            Text(transcript, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            if (state == VoiceState.ERROR) {
                Text("An error occurred. Try again.")
            }

            Spacer(modifier = Modifier.weight(1f))

            Row {
                Button(onClick = { viewModel.cancel() }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.clearTranscript() }) {
                    Text("Clear")
                }
            }
        }
    }
}
