package com.eatssu.notification.service

import com.eatssu.notification.manager.EatSsuNotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EatSsuFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: EatSsuNotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("From: ${remoteMessage.from}")

        val notificationBody = remoteMessage.notification?.body ?: return
        Timber.d("Message Notification Body: $notificationBody")

        notificationManager.showNotification(
            title = null, // Or app name / default title
            body = notificationBody,
            channelId = EatSsuNotificationManager.CHANNEL_SERVER
        )
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        // Token refresh logic if needed, might need a generic event bus or repository interface
        // For now, logging as in original
    }
}
