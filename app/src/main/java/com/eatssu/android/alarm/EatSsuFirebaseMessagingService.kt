package com.eatssu.android.alarm

import com.eatssu.android.R
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.data.remote.repository.NotificationRepository
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.local.TokenStore
import kotlinx.coroutines.flow.first
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class EatSsuFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var publisher: NotificationPublisher

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var settings: SettingDataStore

    @Inject
    lateinit var tokens: TokenStore

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data.toMutableMap().apply {
            put(
                "body",
                remoteMessage.data["body"]?.takeIf { it.isNotBlank() }
                    ?: remoteMessage.notification?.body.orEmpty())
            put(
                "title",
                remoteMessage.data["title"]?.takeIf { it.isNotBlank() }
                    ?: remoteMessage.notification?.title.orEmpty())
        }
        val sentAt = remoteMessage.sentTime.takeIf { it > 0 } ?: System.currentTimeMillis()
        val message = MenuNotification.fromRemoteData(
            data, remoteMessage.messageId ?: "remote_${sentAt}_${data.hashCode()}",
            sentAt, getString(R.string.notification_title),
        ) ?: return
        try {
            // A short local write completes before the FCM callback returns.
            runBlocking(Dispatchers.IO) {
                if (message.serverNotificationId != null &&
                    (tokens.accessToken.isBlank() || !settings.dailyNotificationStatus.first())) return@runBlocking
                publisher.publish(message)
                repository.onPushReceived(message)
            }
        } catch (error: IOException) {
            Timber.w(error, "FCM 알림 저장 실패")
        }
    }

    override fun onNewToken(token: String) {
        // WorkManager retries offline failures and retrieves the latest token when it executes.
        PushTokenSyncWorker.enqueue(this)
    }
}
