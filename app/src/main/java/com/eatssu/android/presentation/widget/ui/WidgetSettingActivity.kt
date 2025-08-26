package com.eatssu.android.presentation.widget.ui

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.presentation.widget.MealWidget
import com.eatssu.android.presentation.widget.MealWorker
import com.eatssu.design_system.theme.EatssuTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

val Context.dataStore by preferencesDataStore(name = "widget_prefs")

@AndroidEntryPoint
class WidgetSettingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {

                val restaurantOptions = Restaurant.getVariableRestaurants().map {
                    it.displayName
                } // 변동 식당만 불러옵니다. 하드코딩 x

                var selectedRestaurant by rememberSaveable { mutableStateOf(restaurantOptions[0]) }

                val appWidgetId = intent?.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                val glanceId: GlanceId? =
                    if (appWidgetId != null && appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        runBlocking {
                            GlanceAppWidgetManager(this@WidgetSettingActivity).getGlanceIdBy(
                                appWidgetId
                            )
                        }
                    } else {
                        null
                    }

                WidgetSettingScreen(
                    restaurantOptionList = restaurantOptions,
                    selectedRestaurant = selectedRestaurant,
                    onSelectRestaurant = { displayName ->
                        selectedRestaurant = displayName
                    },
                    onConfirm = { selectedRestaurantValue ->
                        if (glanceId == null) {
                            finish()
                            return@WidgetSettingScreen
                        }

                        lifecycleScope.launch {

                            saveRestaurantByFileKey(
                                this@WidgetSettingActivity,
                                "appWidget-${appWidgetId}",
                                selectedRestaurantValue
                            )

                            // 위젯 업데이트
                            MealWidget().update(this@WidgetSettingActivity, glanceId)

                            // MealWorker 실행
                            MealWorker.enqueue(this@WidgetSettingActivity)
                            
                            Timber.d("선택하기 버튼으로 저장: $selectedRestaurantValue for glanceId: $glanceId")
                        }

                        // 결과 설정
                        val resultIntent = Intent().apply {
                            putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                appWidgetId ?: AppWidgetManager.INVALID_APPWIDGET_ID
                            )
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }


    companion object {
        private val gson = Gson()

        data class WidgetSettings(
            val restaurant: String = "",
            val appWidgetLayout: String? = null,
        )

        private fun settingsKey(appWidgetId: Int) =
            stringPreferencesKey("widget_settings_$appWidgetId")

        private fun legacyRestaurantKey(appWidgetId: Int) =
            stringPreferencesKey("widget_restaurant_$appWidgetId")

        private fun fileKeyRestaurantKey(fileKey: String) =
            stringPreferencesKey("widget_restaurant_by_fileKey_$fileKey")


        suspend fun saveRestaurantPref(
            context: Context,
            appWidgetId: Int,
            restaurant: String
        ) {
            // Normalize input which may be a display name → store enum name
            val enumName = Restaurant.fromRestaurantEnumName(restaurant) ?: ""
            context.dataStore.edit { prefs ->
                val key = settingsKey(appWidgetId)
                val currentJson = prefs[key]
                val current = runCatching {
                    gson.fromJson(
                        currentJson,
                        WidgetSettings::class.java
                    )
                }.getOrNull() ?: WidgetSettings()
                val updated = current.copy(restaurant = enumName)
                prefs[key] = gson.toJson(updated)
                // clean legacy
                prefs.remove(legacyRestaurantKey(appWidgetId))
            }
            Timber.d("save restaurant $enumName (from input '$restaurant') for appWidgetId $appWidgetId")
        }

        suspend fun saveRestaurantByFileKey(
            context: Context,
            fileKey: String,
            restaurant: String,
        ) {
//            val enumName = Restaurant.fromRestaurantEnumName(restaurant) ?: ""
            context.dataStore.edit { prefs ->
                prefs[fileKeyRestaurantKey(fileKey)] = restaurant
            }
            Timber.d("saveRestaurantByFileKey 호출됨: fileKey='$fileKey', restaurant='$restaurant'")
        }

        suspend fun loadRestaurantByFileKey(context: Context, fileKey: String): Restaurant? {
            val prefs: Preferences = context.dataStore.data.first()
            val value = prefs[fileKeyRestaurantKey(fileKey)]
            Timber.d("loadRestaurantByFileKey 호출됨: fileKey='$fileKey', value='$value'")
            if (value.isNullOrBlank()) {
                return null
            }
            return runCatching { Restaurant.valueOf(value) }.getOrNull()
        }


        suspend fun loadRestaurantPref(context: Context, appWidgetId: Int): Restaurant {
            val prefs: Preferences = context.dataStore.data.first()
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
            // fallback to legacy key
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

    }
}
