package com.eatssu.android.presentation.widget.ui

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "widget_prefs")

class WidgetSettingActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        setContent {
            EatssuTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WidgetSettingScreen(

                        onConfirm = { selectedRestaurant ->
                            // DataStore에 저장
                            lifecycleScope.launch {
                                saveRestaurantPref(
                                    this@WidgetSettingActivity,
                                    appWidgetId,
                                    Restaurant.entries[selectedRestaurant]
                                )
                                val resultValue = Intent().apply {
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                }
                                setResult(RESULT_OK, resultValue)
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }

    private suspend fun saveRestaurantPref(
        context: Context,
        appWidgetId: Int,
        restaurant: Restaurant
    ) {
        val key = stringPreferencesKey("widget_restaurant_$appWidgetId")
        context.dataStore.edit { prefs ->
            prefs[key] = restaurant.name
        }
    }

    companion object {
        suspend fun loadRestaurantPref(context: Context, appWidgetId: Int): Restaurant {
            val key = stringPreferencesKey("widget_restaurant_$appWidgetId")
            val prefs: Preferences = context.dataStore.data.first()
            val value = prefs[key] ?: Restaurant.DODAM.name
            return Restaurant.valueOf(value)
        }

        suspend fun saveRestaurantPref(context: Context, appWidgetId: Int, restaurant: Restaurant) {
            val key = stringPreferencesKey("widget_restaurant_$appWidgetId")
            context.dataStore.edit { prefs ->
                prefs[key] = restaurant.name
            }
        }
    }
}
