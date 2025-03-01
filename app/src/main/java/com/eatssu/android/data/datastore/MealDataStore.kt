package com.eatssu.android.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eatssu.android.data.datastore.DatastoreUtil.mealDataStore
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

    suspend fun saveMeal(meals: List<GetMealResponse>) {
        dataStore.edit { prefs ->
            prefs[MEAL_KEY] = meals.toJson()  // 항상 리스트로 저장
        }
    }

    // Flow로 저장된 데이터를 가져올 때 역직렬화
    fun getMealFlow(): Flow<ArrayList<GetMealResponse>> {
        return dataStore.data.map { prefs ->
            prefs[MEAL_KEY]?.fromJsonArray()
                ?: ArrayList()  // JSON을 ArrayList<GetMealResponse>로 변환
        }
    }
}
