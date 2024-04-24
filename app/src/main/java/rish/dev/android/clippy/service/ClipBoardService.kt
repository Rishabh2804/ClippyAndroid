package rish.dev.android.clippy.service

import android.app.Service
import android.content.ClipData
import android.content.Intent
import android.os.IBinder
import rish.dev.android.clippy.model.Clip
import rish.dev.android.clippy.model.ClipType

class ClipBoardService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createConnection()
    }

    private fun createConnection() {

    }

    private fun parseClipData(clipData: ClipData?): Clip? {
        if(clipData == null || clipData.itemCount == 0) return null

        val primaryClip = clipData.getItemAt(0);
        val clipText = primaryClip.text
        val clipHtmlText = primaryClip.htmlText
        val clipUri = primaryClip.uri
        val clipIntent = primaryClip.intent

        return Clip(clipText.toString(), ClipType.TEXT)
    }

}