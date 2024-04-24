package rish.dev.android.clippy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rish.dev.android.clippy.model.Clip
import rish.dev.android.clippy.model.ClipType
import rish.dev.android.clippy.socket.SocketListener
import rish.dev.android.clippy.socket.SocketManager

class ClippyVM : ViewModel() {
    private val _messages = MutableLiveData<List<Clip>>()
    val messages: LiveData<List<Clip>> = _messages

    private var socketManager: SocketManager? = null
    private val socketListener = SocketListener(
        onMessage = { clip ->
            clip?.let {
                _messages.value = _messages.value?.plus(clip)
            }
        }
    )

    fun connect() {
        if (socketManager == null) socketManager = SocketManager()

        socketManager?.connect(
            onMessage = {
//                _messages.value = _messages.value?.plus(it)
                Log.d("ClippyVM", "Message received : $it")
            }
        )
    }

    fun sendMessage(clip: Clip) {
        socketManager?.sendMessage(clip) ?: run {
//            _messages.value = _messages.value?.plus(socketNotConnected)
        }
    }

    fun disconnect() {
        socketManager?.closeWebSocket()
        socketManager = null
    }

    private val socketNotConnected = Clip("Socket not connected", ClipType.UNKNOWN)
}