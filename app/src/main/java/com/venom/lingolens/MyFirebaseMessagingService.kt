package com.venom.lingolens

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { sendNotification(it) }
    }

    private fun sendNotification(notification: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(com.venom.resources.R.string.default_notification_channel_id)

//        val customContentView = android.widget.RemoteViews(packageName, R.layout.notification_small)
//        val customBigContentView = android.widget.RemoteViews(packageName, R.layout.notification_expanded)


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            // Basic
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setContentIntent(pendingIntent)
            // Additional
            .setColor(notification.color?.toInt() ?: 0)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setChannelId(notification.channelId ?: channelId)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Custom views
//            .setCustomContentView(customContentView)
//            .setCustomBigContentView(customBigContentView)

        getSystemService(NotificationManager::class.java)?.notify(
            notification.tag?.hashCode() ?: 0,
            notificationBuilder.build()
        )
    }
}