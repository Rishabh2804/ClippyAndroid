package rish.dev.android.clippy.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import rish.dev.android.clippy.R
import rish.dev.android.clippy.model.Clip

class ClippyNotification(private val context: Context) {

    companion object {
        const val CLIP_NOTIF_ID = 1
        const val SERVICE_NOTIF_ID = 2

        private const val CHANNEL_ID = "network_speed_channel"
        private const val CHANNEL_NAME = "Network Speed"
        private const val TITLE = "Network Speed"
        private const val DESCRIPTION = "This channel is used to display network speed"

        private val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = DESCRIPTION
            }

        fun destroyNotification(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(CLIP_NOTIF_ID)
        }
    }

    private val clippyNotifBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clippy_tile)
            .setContentTitle(TITLE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(false)
//            .setShowWhen(false)
//            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


    fun serviceNotification() : Notification? {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)

        clippyNotifBuilder.setContentText(context.getString(R.string.title_clippy_service))

        if (!checkPermission(context)) {
            Toast.makeText(context, context.getString(R.string.enable_notif_permit), Toast.LENGTH_SHORT).show()
            return null
        }

//        with(notificationManager) {
//            notify(SERVICE_NOTIF_ID, clippyNotifBuilder.build())
//        }
//
        return clippyNotifBuilder.build()
    }

    fun newClipReceivedNotification(clip: Clip) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)

        clippyNotifBuilder.setContentText(context.getString(R.string.title_clip_received, clip.clipData))

        if (!checkPermission(context)) {
            Toast.makeText(context, context.getString(R.string.enable_notif_permit), Toast.LENGTH_SHORT).show()
            return
        }

        with(notificationManager) {
            notify(CLIP_NOTIF_ID, clippyNotifBuilder.setAutoCancel(true).build())
        }
    }

    fun dismiss(){
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll()
    }

    private fun checkPermission(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

}