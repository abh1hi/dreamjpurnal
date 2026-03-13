package com.example.dream_jpurnal

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContent {
            var recordings by remember { mutableStateOf(getRecordings()) }
            var isRecordingActive by remember { mutableStateOf(false) } // This is just for UI feedback here

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Dream Recorder",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Permissions & Settings
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    requestPermissionLauncher.launch(permissions.toTypedArray())
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Permissions")
                            }

                            Button(
                                onClick = {
                                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Accessibility")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Controls
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Manual Controls", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { sendAction("START") }) { Text("Start") }
                                    Button(onClick = { sendAction("PAUSE") }) { Text("Pause") }
                                    Button(onClick = { sendAction("RESUME") }) { Text("Resume") }
                                    Button(onClick = { 
                                        sendAction("STOP")
                                        recordings = getRecordings()
                                    }) { Text("Stop") }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "Recent Recordings", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(recordings) { file ->
                                RecordingItem(file) {
                                    playRecording(file)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getRecordings(): List<File> {
        val directory = getExternalFilesDir(null)
        return directory?.listFiles { file -> file.extension == "3gp" }?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    private fun sendAction(action: String) {
        val intent = Intent(this, RecordingService::class.java).apply {
            this.action = action
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun playRecording(file: File) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

@Composable
fun RecordingItem(file: File, onListen: () -> Unit) {
    ListItem(
        headlineContent = { Text(file.name) },
        supportingContent = { Text("${file.length() / 1024} KB") },
        trailingContent = {
            IconButton(onClick = onListen) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
        },
        modifier = Modifier.clickable { onListen() }
    )
}