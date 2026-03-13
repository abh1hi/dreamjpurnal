package com.example.dream_jpurnal

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class GestureAccessibilityService : AccessibilityService() {

    private var upPressed = false
    private var downPressed = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            upPressed = event.action == KeyEvent.ACTION_DOWN
        }

        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            downPressed = event.action == KeyEvent.ACTION_DOWN
        }

        if (upPressed && downPressed) {
            val intent = Intent(this, RecordingService::class.java)
            intent.action = "START"

            if (android.os.Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            // reset state
            upPressed = false
            downPressed = false
            
            // Consume the event that triggered the recording
            return true
        }

        // Do not consume normal volume button presses
        return super.onKeyEvent(event)
    }
}