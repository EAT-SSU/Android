package com.eatssu.android.data.datastore

import com.eatssu.android.data.dto.response.GetMealResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GsonUtil {

    fun <T> T.toJson(): String {
        return Gson().toJson(this)
    }

    inline fun <reified T> String.fromJson(): T {
        return Gson().fromJson(this, T::class.java)
    }

    fun String.fromJsonArray(): ArrayList<GetMealResponse> {
        val type = object : TypeToken<ArrayList<GetMealResponse>>() {}.type
        return Gson().fromJson(this, type)
    }
}