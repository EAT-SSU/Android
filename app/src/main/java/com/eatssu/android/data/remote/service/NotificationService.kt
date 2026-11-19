package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.LanguageRequest
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationService {
    @GET("notifications")
    suspend fun getNotifications(): ApiResult<List<ServerNotificationResponse>>

    @GET("notifications/unread")
    suspend fun getUnread(): ApiResult<UnreadNotificationResponse>

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: Long): ApiResult<Unit>

    @PATCH("notifications/read-all")
    suspend fun markAllRead(): ApiResult<Unit>
}

// Explicit authorization avoids invoking the logout authenticator while revoking a token.
interface PushTokenService {
    @PATCH("users/language")
    suspend fun updateLanguage(
        @Header("Authorization") authorization: String,
        @Body request: LanguageRequest,
    ): ApiResult<Unit>

    @PUT("users/push-token")
    suspend fun register(
        @Header("Authorization") authorization: String,
        @Body request: PushTokenRequest,
    ): ApiResult<Unit>

    @HTTP(method = "DELETE", path = "users/push-token", hasBody = true)
    suspend fun delete(
        @Header("Authorization") authorization: String,
        @Body request: DeletePushTokenRequest,
    ): ApiResult<Unit>
}

@Serializable
data class PushTokenRequest(val token: String, val deviceType: String)

@Serializable
data class DeletePushTokenRequest(val token: String)

@Serializable
data class UnreadNotificationResponse(val hasUnread: Boolean)

@Serializable
data class ServerNotificationResponse(
    val notificationId: Long,
    val type: String,
    val title: String,
    val body: String,
    val date: String,
    val timePart: String,
    val isRead: Boolean,
    val createdAt: String,
)
