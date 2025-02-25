package com.eatssu.android.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eatssu.android.data.datastore.DatastoreUtil.mealDataStore
import com.eatssu.android.data.datastore.GsonUtil.fromJsonArray
import com.eatssu.android.data.datastore.GsonUtil.toJson
import com.eatssu.android.data.dto.response.GetMealResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MealDataStore @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.mealDataStore

    companion object {
        private val MEAL_KEY = stringPreferencesKey("meal_data")
    }

    // meal을 JSON으로 변환하여 저장
    suspend fun saveMeal(meal: ArrayList<GetMealResponse>) {
        dataStore.edit { prefs ->
            prefs[MEAL_KEY] = meal.toJson()  // ArrayList<GetMealResponse>를 JSON으로 변환
        }
    }

    // Flow로 저장된 데이터를 가져올 때 역직렬화
    fun getMealFlow(): Flow<ArrayList<GetMealResponse>> {
        return dataStore.data.map { prefs ->
            prefs[MEAL_KEY]?.let { it.fromJsonArray() }
                ?: ArrayList()  // JSON을 ArrayList<GetMealResponse>로 변환
        }
    }
}
