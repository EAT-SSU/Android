package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.appFeatureDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_feature")

class AppFeatureDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val ANYONE_BUT_ME_EVENT_POPUP_DISMISSED =
            booleanPreferencesKey("anyone_but_me_event_popup_dismissed")
    }

    val isAnyoneButMeEventPopupDismissed: Flow<Boolean> = context.appFeatureDataStore.data
        .map { preferences ->
            preferences[ANYONE_BUT_ME_EVENT_POPUP_DISMISSED] ?: false
        }

    suspend fun setAnyoneButMeEventPopupDismissed(dismissed: Boolean) {
        context.appFeatureDataStore.edit { preferences ->
            preferences[ANYONE_BUT_ME_EVENT_POPUP_DISMISSED] = dismissed
        }
    }
}
