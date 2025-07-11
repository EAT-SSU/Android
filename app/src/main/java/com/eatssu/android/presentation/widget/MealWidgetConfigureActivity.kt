package com.eatssu.android.presentation.widget.we

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

class MealWidgetConfigureActivity : ComponentActivity() {
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
                    WidgetConfigScreen(
                        onConfirm = { selectedRestaurant ->
                            // DataStore에 저장
                            lifecycleScope.launch {
                                saveRestaurantPref(
                                    this@MealWidgetConfigureActivity,
                                    appWidgetId,
                                    selectedRestaurant
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

@Composable
fun WidgetConfigScreen(onConfirm: (Restaurant) -> Unit) {
    var selected by remember { mutableStateOf(Restaurant.DODAM) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("위젯에 표시할 식당을 선택하세요")
        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Restaurant.values()
                .filter { it in listOf(Restaurant.HAKSIK, Restaurant.DODAM, Restaurant.DORMITORY) }
                .forEach { restaurant ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selected == restaurant,
                            onClick = { selected = restaurant },
                            colors = RadioButtonDefaults.colors()
                        )
                        Text(restaurant.displayName + " (${restaurant.name})")
                    }
                }
        }
        Button(
            onClick = { onConfirm(selected) },
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text("확인")
        }
    }
}


@Composable
@Preview
fun WidgetConfigScreenPreview() {
    WidgetConfigScreen(onConfirm = {})
}