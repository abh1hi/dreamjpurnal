package com.example.dream_jpurnal

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class RecorderWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: IntArray) {
        // This is for multiple widgets
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.recorder_widget)

        // Start Intent
        val startIntent = Intent(context, RecordingService::class.java).apply { action = "START" }
        val startPendingIntent = PendingIntent.getService(
            context, 0, startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_widget_start, startPendingIntent)

        // Stop Intent
        val stopIntent = Intent(context, RecordingService::class.java).apply { action = "STOP" }
        val stopPendingIntent = PendingIntent.getService(
            context, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_widget_stop, stopPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
