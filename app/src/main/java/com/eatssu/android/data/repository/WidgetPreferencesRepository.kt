package com.eatssu.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eatssu.android.data.enums.Restaurant
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetPreferencesRepository @Inject constructor(
    private val context: Context,
) {

    private val gson = Gson()

    // Local to this file; avoids leaking as a global extension
    private val Context.widgetPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_prefs")

    private fun settingsKey(appWidgetId: Int) = stringPreferencesKey("widget_settings_$appWidgetId")
    private fun legacyRestaurantKey(appWidgetId: Int) =
        stringPreferencesKey("widget_restaurant_$appWidgetId")

    private fun fileKeyRestaurantKey(fileKey: String) =
        stringPreferencesKey("widget_restaurant_by_fileKey_$fileKey")

    suspend fun saveRestaurantByFileKey(fileKey: String, restaurant: String) {
        context.widgetPrefsDataStore.edit { prefs ->
            prefs[fileKeyRestaurantKey(fileKey)] = restaurant
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

    suspend fun loadRestaurantPref(appWidgetId: Int): Restaurant {
        val prefs: Preferences = context.widgetPrefsDataStore.data.first()
        val json = prefs[settingsKey(appWidgetId)]
        if (!json.isNullOrBlank()) {
            val settings =
                runCatching { gson.fromJson(json, WidgetSettings::class.java) }.getOrNull()
            if (settings != null && settings.restaurant.isNotBlank()) {
                val enumName = Restaurant.fromDisplayName(settings.restaurant)
                Timber.d("load restaurant from settings $enumName")
                return Restaurant.valueOf(enumName)
            }
        }
        val legacyRaw = prefs[legacyRestaurantKey(appWidgetId)] ?: ""
        val legacyEnumName = Restaurant.fromDisplayName(legacyRaw)
        return if (legacyEnumName.isNotBlank()) {
            Timber.d("load restaurant (legacy) $legacyEnumName from '$legacyRaw' for appWidgetId $appWidgetId")
            Restaurant.valueOf(legacyEnumName)
        } else {
            Timber.w("No saved restaurant for appWidgetId=$appWidgetId, defaulting to HAKSIK")
            Restaurant.HAKSIK
        }
    }

    data class WidgetSettings(
        val restaurant: String = "",
        val appWidgetLayout: String? = null,
    )
} 