// PreferencesRepository.kt
package com.eatssu.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object {
        private val DAILY_NOTIFICATION_KEY = booleanPreferencesKey("daily_notification")
    }

    val dailyNotificationStatus: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DAILY_NOTIFICATION_KEY] ?: false // Default value is false
        }

    suspend fun setDailyNotificationStatus(status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_NOTIFICATION_KEY] = status
        }
    }
}
