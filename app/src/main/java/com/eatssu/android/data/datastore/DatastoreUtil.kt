package com.eatssu.android.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DatastoreUtil {
    val Context.mealDataStore: DataStore<Preferences> by preferencesDataStore(name = "meal_data_store")

}