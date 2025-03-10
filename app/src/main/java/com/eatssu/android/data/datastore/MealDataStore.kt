package com.eatssu.android.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eatssu.android.data.datastore.DatastoreUtil.mealDataStore
import com.eatssu.android.domain.model.WidgetMeal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MealDataStore @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.mealDataStore

    val mealFlow: Flow<WidgetMeal> = dataStore.data
        .map { preferences ->
            val restaurantName = preferences[PreferencesKeys.RESTAURANT_NAME] ?: ""
            val date = preferences[PreferencesKeys.DATE] ?: ""
            val time = preferences[PreferencesKeys.TIME] ?: ""
            val menuList = preferences[PreferencesKeys.MENU_LIST]?.split(",") ?: listOf()
            WidgetMeal(restaurantName, date, time, menuList)
        }

    suspend fun saveMeal(
        date: String,
        restaurant: String,
        time: String,
        mealList: List<String>
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RESTAURANT_NAME] = restaurant
            preferences[PreferencesKeys.DATE] = date
            preferences[PreferencesKeys.TIME] = time
            preferences[PreferencesKeys.MENU_LIST] = mealList.joinToString(",")
        }
    }

    object PreferencesKeys {
        val RESTAURANT_NAME = stringPreferencesKey("restaurant_name")
        val DATE = stringPreferencesKey("date")
        val TIME = stringPreferencesKey("time")
        val MENU_LIST = stringPreferencesKey("menu_list")
    }
}
