package com.example.dream_jpurnal

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class RecordingTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        // We can check RecordingService status here
        // For now, we'll toggle it based on the action
    }

    override fun onClick() {
        super.onClick()
        val tile = qsTile ?: return

        if (tile.state == Tile.STATE_ACTIVE) {
            // STOP
            sendAction("STOP")
            tile.state = Tile.STATE_INACTIVE
            tile.label = "Start Recording"
        } else {
            // START
            sendAction("START")
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Stop Recording"
        }
        tile.updateTile()
    }

    private fun sendAction(action: String) {
        val intent = Intent(this, RecordingService::class.java).apply {
            this.action = action
        }
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
