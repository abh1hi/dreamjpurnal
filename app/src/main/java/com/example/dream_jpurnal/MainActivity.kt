package com.example.dream_jpurnal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Dream Recorder",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        Button(
                            onClick = {
                                val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                requestPermissionLauncher.launch(permissions.toTypedArray())
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text("Grant Permissions (Mic & Notifications)")
                        }

                        Button(
                            onClick = {
                                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enable Accessibility Service (Vol Up/Down trigger)")
                        }
                    }
                }
            }
        }
    }
}