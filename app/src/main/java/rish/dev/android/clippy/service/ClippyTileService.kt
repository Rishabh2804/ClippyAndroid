package rish.dev.android.clippy.service

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import rish.dev.android.clippy.R

class ClippyTileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        val tile = qsTile
        tile.state = Tile.STATE_INACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        updateService()
    }

    private fun updateService() {
        val tile = qsTile
        when (tile.state) {
            Tile.STATE_ACTIVE -> {
                stopService()

                tile.state = Tile.STATE_INACTIVE
                tile.icon = Icon.createWithResource(
                    this, R.drawable.ic_clippy_tile
                )
            }

            Tile.STATE_INACTIVE -> {
                startService()

                tile.state = Tile.STATE_ACTIVE
                tile.icon = Icon.createWithResource(this, R.drawable.ic_clippy_tile)
            }
        }

        tile.updateTile()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        val tile = qsTile
        tile.state = Tile.STATE_INACTIVE
        tile.updateTile()
    }

    private fun startService(){
        ClipBoardService.startService(this)
    }

    private fun stopService(){
        ClipBoardService.stopService(this)
    }
}