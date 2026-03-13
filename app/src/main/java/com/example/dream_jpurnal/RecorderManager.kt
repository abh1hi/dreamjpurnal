package com.example.dream_jpurnal

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class RecorderManager(private val context: Context) {

    private var recorder: MediaRecorder? = null

    fun startRecording(): File {

        val file = File(
            context.getExternalFilesDir(null),
            "dream_${System.currentTimeMillis()}.3gp"
        )

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder?.apply {

            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)

            prepare()
            start()
        }

        return file
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.pause()
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.resume()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            try {
                stop()
            } catch (e: Exception) {
                // Handle cases where stop is called without enough data
            }
            release()
        }
        recorder = null
    }
}