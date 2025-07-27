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
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.widget.MealWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

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
                    onConfirm = { selectedRestaurantValue ->
                        val appWidgetId = intent?.getIntExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID
                        )
                        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                            finish()
                            return@WidgetSettingScreen
                        }

                        lifecycleScope.launch {
                            if (appWidgetId != null) {
                                saveRestaurantPref(
                                    this@WidgetSettingActivity,
                                    appWidgetId,
                                    selectedRestaurantValue
                                )

                                val glanceId = GlanceAppWidgetManager(this@WidgetSettingActivity)
                                    .getGlanceIds(MealWidget::class.java)
                                    .firstOrNull { it.hashCode() == appWidgetId }

                                if (glanceId != null) {
                                    MealWidget().update(this@WidgetSettingActivity, glanceId)
                                }
//                                MealWidget().update(context = this@WidgetSettingActivity, appWidgetId)

                            }

                            val resultIntent = Intent().apply {
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            }
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
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

            Timber.d("save $restaurant")
        }
        suspend fun loadRestaurantPref(context: Context, appWidgetId: Int): Restaurant {
            val key = stringPreferencesKey("widget_restaurant_$appWidgetId")
            val prefs: Preferences = context.dataStore.data.first()
            val value = prefs[key] ?: Restaurant.DODAM.name

            Timber.d("load $value")

            return Restaurant.valueOf(value)
        }

    }


}
