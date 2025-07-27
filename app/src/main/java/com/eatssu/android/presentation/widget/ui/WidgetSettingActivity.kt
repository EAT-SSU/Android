package com.eatssu.android.presentation.widget.ui

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "widget_prefs")

@AndroidEntryPoint
class WidgetSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {
                val restaurantOptions = listOf("학생 식당", "도담 식당", "기숙사 식당")
                var selectedRestaurant by rememberSaveable { mutableStateOf(restaurantOptions[0]) }

                WidgetSettingScreen(
                    restaurantOptions = restaurantOptions,
                    selectedRestaurant = selectedRestaurant,
                    onSelectRestaurant = { selectedRestaurant = it },
                    onConfirm = { index ->
                        val appWidgetId = intent?.getIntExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID
                        )
                        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                            finish()
                            return@WidgetSettingScreen
                        }

                        // ✅ 위젯 설정 저장 (예: DataStore에 index나 Restaurant 저장)

                        lifecycleScope.launch {

                            if (appWidgetId != null) {
                                saveRestaurantPref(
                                    this@WidgetSettingActivity,
                                    appWidgetId,
                                    selectedRestaurant
                                )
                            }
                        }
                        // ✅ 시스템에 설정 완료 알리기
                        val resultIntent = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        setResult(RESULT_OK, resultIntent)

                        // ✅ 종료
                        finish()
                    }

                )
            }
        }
    }


    companion object {

        suspend fun saveRestaurantPref(
            context: Context,
            appWidgetId: Int,
            restaurant: String
        ) {
            val key = stringPreferencesKey("widget_restaurant_$appWidgetId")
            context.dataStore.edit { prefs ->
                prefs[key] = restaurant
            }
        }
        suspend fun loadRestaurantPref(context: Context, appWidgetId: Int): Restaurant {
            val key = stringPreferencesKey("widget_restaurant_$appWidgetId")
            val prefs: Preferences = context.dataStore.data.first()
            val value = prefs[key] ?: Restaurant.DODAM.name
            return Restaurant.valueOf(value)
        }

    }


}
