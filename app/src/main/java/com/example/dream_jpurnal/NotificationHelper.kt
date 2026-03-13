package com.example.dream_jpurnal

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

object NotificationHelper {

    const val CHANNEL = "record_channel"

    fun createChannel(context: Context) {

        val manager =
            context.getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL,
            "Recording",
            NotificationManager.IMPORTANCE_LOW
        )

        manager.createNotificationChannel(channel)
    }

    fun createNotification(context: Context, isPaused: Boolean): Notification {

        val stopIntent = Intent(context, RecordingService::class.java).apply { action = "STOP" }
        val pendingStop = PendingIntent.getService(context, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val pauseResumeIntent = Intent(context, RecordingService::class.java).apply {
            action = if (isPaused) "RESUME" else "PAUSE"
        }
        val pendingPauseResume = PendingIntent.getService(context, 2, pauseResumeIntent, PendingIntent.FLAG_IMMUTABLE)

        val text = if (isPaused) "Recording paused" else "Recording dream..."
        val pauseResumeLabel = if (isPaused) "Resume" else "Pause"
        val pauseResumeIcon = if (isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause

        return NotificationCompat.Builder(context, CHANNEL)
            .setContentTitle("Dream Recorder")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .addAction(pauseResumeIcon, pauseResumeLabel, pendingPauseResume)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", pendingStop)
            .setOngoing(true)
            .build()
    }
}