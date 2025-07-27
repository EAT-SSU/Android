package com.eatssu.android.presentation.widget.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

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
                        Toast.makeText(this, "선택한 인덱스: $index", Toast.LENGTH_SHORT).show()
                        finish() // 예: 완료 후 액티비티 종료
                    }
                )
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
