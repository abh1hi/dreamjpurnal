package com.example.dream_jpurnal

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class GestureAccessibilityService : AccessibilityService() {

    private var lastUpPressTime = 0L
    private var lastDownPressTime = 0L
    private val SIMULTANEOUS_THRESHOLD_MS = 300L

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
            flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }
        this.serviceInfo = info
        Toast.makeText(this, "Dream Recorder Trigger Active", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val action = event.action

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Diagnostic toast (optional, keep it brief)
            // Toast.makeText(this, "Key: $keyCode, Action: $action", Toast.LENGTH_SHORT).show()

            if (action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) lastUpPressTime = System.currentTimeMillis()
                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) lastDownPressTime = System.currentTimeMillis()

                val timeDiff = Math.abs(lastUpPressTime - lastDownPressTime)
                if (timeDiff < SIMULTANEOUS_THRESHOLD_MS && lastUpPressTime != 0L && lastDownPressTime != 0L) {
                    triggerRecording()
                    // Reset to prevent double trigger
                    lastUpPressTime = 0L
                    lastDownPressTime = 0L
                    return true // Consume
                }
            }
        }

        return super.onKeyEvent(event)
    }

    private fun triggerRecording() {
        Toast.makeText(this, "Triggering Recording & Opening App...", Toast.LENGTH_LONG).show()

        // 1. Start Service
        val serviceIntent = Intent(this, RecordingService::class.java).apply {
            action = "START"
        }
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // 2. Open App
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(launchIntent)
    }
}