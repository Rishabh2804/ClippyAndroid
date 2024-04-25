package rish.dev.android.clippy.service

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import rish.dev.android.clippy.lock.AttemptResult
import rish.dev.android.clippy.lock.ClippyLock
import rish.dev.android.clippy.lock.LockException
import rish.dev.android.clippy.model.Clip
import rish.dev.android.clippy.model.ClipType
import rish.dev.android.clippy.notification.ClippyNotification
import rish.dev.android.clippy.socket.SocketManager

class ClipBoardService : Service() {

    private var socketManager: SocketManager? = null
    private var clipboardManager: ClipboardManager? = null
    private var clippyNotification: ClippyNotification? = null

    private var sendingLock: ClippyLock<Clip>? = null
    private var receivingLock: ClippyLock<Clip>? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createConnection()
        startForeground(ClippyNotification.SERVICE_NOTIF_ID, clippyNotification?.serviceNotification())
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createConnection() {
        try {
            clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            socketManager = SocketManager()
            clippyNotification = ClippyNotification(this)

            sendingLock = ClippyLock(SENDING_LOCK)
            receivingLock = ClippyLock(RECEIVING_LOCK)

            listenToServer()
            listenToClipBoard()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun listenToServer() {
        socketManager?.connect(this::onNewClip)
    }

    private fun onNewClip(clip: Clip) {
        clip.let {
            try {
                checkLocks(
                    inLock = receivingLock ?: return@onNewClip,
                    outLock = sendingLock ?: return@onNewClip,
                    key = it,
                    onFail = { e -> throw e }
                )

                when (clip.clipType) {
                    ClipType.TEXT -> {
                        clipboardManager?.setPrimaryClip(ClipData.newPlainText("text", clip.clipData))
                    }

                    ClipType.IMAGE -> Unit /** TODO: Implement image handling **/
                    else -> Unit /* no-op */
                }

                Log.d(TAG, "New clip received: $clip")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun listenToClipBoard() {
        clipboardManager?.addPrimaryClipChangedListener {

            val clipData = clipboardManager?.primaryClip
            val clip = parseClipData(clipData)
            clip?.let {
                try {
                    checkLocks(
                        inLock = sendingLock ?: return@addPrimaryClipChangedListener,
                        outLock = receivingLock ?: return@addPrimaryClipChangedListener,
                        key = it,
                        onFail = { e -> throw e }
                    )

                    socketManager?.sendMessage(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager?.closeWebSocket()
        socketManager = null
        clipboardManager = null

        sendingLock = null
        receivingLock = null

        clippyNotification?.dismiss()
        clippyNotification = null
    }

    private fun checkLocks(
        inLock: ClippyLock<Clip>,
        outLock: ClippyLock<Clip>,
        key: Clip,
        onFail: (LockException) -> Unit
    ) {
        inLock.logStatus()
        when (val attempt = inLock.unlockWith(key)) {
            is AttemptResult.Clear -> Unit /* no-op */
            else -> onFail(attempt.outcome!!)
        }

        outLock.logStatus()
        outLock.lockWith(key)
    }

    private fun parseClipData(clipData: ClipData?): Clip? {
        if (clipData == null || clipData.itemCount == 0) return null

        val primaryClip = clipData.getItemAt(0)
        val clipText = primaryClip.text

        return Clip(clipText.toString(), ClipType.TEXT)
    }

    companion object {
        const val TAG = "ClipBoardService"
        private const val RECEIVING_LOCK = "ReceivingLock"
        private const val SENDING_LOCK = "SendingLock"

        fun startService(context: Context) {
            val serviceIntent = Intent(context, ClipBoardService::class.java)
            context.startService(serviceIntent)
        }

        fun stopService(context: Context) {
            val serviceIntent = Intent(context, ClipBoardService::class.java)
            context.stopService(serviceIntent)
        }
    }
}

