package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eatssu.common.enums.Restaurant
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.widgetPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_prefs")

@Singleton
class WidgetDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private fun fileKeyRestaurantKey(fileKey: String) =
        stringPreferencesKey("widget_restaurant_by_fileKey_$fileKey")

    suspend fun saveRestaurantByFileKey(fileKey: String, restaurant: Restaurant) {
        context.widgetPrefsDataStore.edit { prefs ->
            prefs[fileKeyRestaurantKey(fileKey)] = restaurant.name
        }
        Timber.d("saveRestaurantByFileKey 호출됨: fileKey='$fileKey', restaurant='$restaurant'")
    }

    suspend fun loadRestaurantByFileKey(fileKey: String): Restaurant? {
        val prefs: Preferences = context.widgetPrefsDataStore.data.first()
        val value = prefs[fileKeyRestaurantKey(fileKey)]
        Timber.d("loadRestaurantByFileKey 호출됨: fileKey='$fileKey', value='$value'")
        if (value.isNullOrBlank()) return null
        return runCatching { Restaurant.valueOf(value) }.getOrNull()
    }
}
