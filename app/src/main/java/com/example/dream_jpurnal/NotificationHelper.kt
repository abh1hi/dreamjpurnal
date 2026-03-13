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

    fun createNotification(context: Context): Notification {

        val stopIntent = Intent(context, RecordingService::class.java)
        stopIntent.action = "STOP"

        val pendingStop = PendingIntent.getService(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL)
            .setContentTitle("Dream Recorder")
            .setContentText("Recording dream...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .addAction(android.R.drawable.ic_delete,"Stop",pendingStop)
            .setOngoing(true)
            .build()
    }
}