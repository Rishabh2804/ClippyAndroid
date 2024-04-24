package rish.dev.android.clippy.socket

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import rish.dev.android.clippy.model.Clip

class SocketListener(
    private val onMessage: (Clip?) -> Unit = {},
) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        logAndShow("Connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val clip : Clip? = Gson().fromJson(text, Clip::class.java)

        onMessage(clip)
        logAndShow("Received: $clip")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        logAndShow("Failed to connect")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        logAndShow("Closing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        logAndShow("Closed")

    }

    private fun logAndShow(message : String){
        Log.d("SocketListener", message)
    }
}