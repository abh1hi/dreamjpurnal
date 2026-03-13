package com.example.dream_jpurnal

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RecordingService : Service() {

    private lateinit var recorder: RecorderManager

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createChannel(this)
        recorder = RecorderManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){

            "START" -> startRecording()
            "STOP" -> stopRecording()
            "PAUSE" -> pauseRecording()
            "RESUME" -> resumeRecording()
        }

        return START_STICKY
    }

    private var isRecording = false

    private fun startRecording() {
        if (isRecording) return

        startForeground(
            1,
            NotificationHelper.createNotification(this, false)
        )

        recorder.startRecording()
        isRecording = true
    }

    private fun pauseRecording() {
        if (!isRecording) return
        recorder.pauseRecording()
        // Update notification to show "Paused"
        startForeground(1, NotificationHelper.createNotification(this, true))
    }

    private fun resumeRecording() {
        if (!isRecording) return
        recorder.resumeRecording()
        // Update notification to show "Recording"
        startForeground(1, NotificationHelper.createNotification(this, false))
    }

    private fun stopRecording(){
        if(!isRecording) return

        recorder.stopRecording()

        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRecording = false
    }

    override fun onBind(intent: Intent?): IBinder? = null
}