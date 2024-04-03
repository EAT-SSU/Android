package com.eatssu.android.data.repository

interface PreferencesRepository {
    suspend fun getStoryCompleted(): Boolean

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?
    suspend fun updateStoryCompleted(storyCompleted: Boolean)

    suspend fun updateAccessToken(accessToken: String?)

    suspend fun updateRefreshToken(refreshToken: String?)

}