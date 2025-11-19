package com.eatssu.android.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(
    private val securePrefs: SharedPreferences
) {
    private companion object {
        const val KEY_ACCESS = "ACCESS_TOKEN"
        const val KEY_REFRESH = "REFRESH_TOKEN"
    }

    var accessToken: String
        get() = securePrefs.getString(KEY_ACCESS, "").orEmpty()
        set(v) = securePrefs.edit { putString(KEY_ACCESS, v) }

    var refreshToken: String
        get() = securePrefs.getString(KEY_REFRESH, "").orEmpty()
        set(v) = securePrefs.edit { putString(KEY_REFRESH, v) }

    fun clear() = securePrefs.edit { remove(KEY_ACCESS); remove(KEY_REFRESH) }
}
