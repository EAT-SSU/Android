package com.eatssu.android.data.datastore

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken


fun <T> T.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, T::class.java)
}

inline fun <reified T> String.fromJsonArray(): ArrayList<T> {
    return try {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<T>>() {}.type
        gson.fromJson(this, type)
    } catch (e: JsonSyntaxException) {
        try {
            // JSON이 배열이 아니라 단일 객체라면 리스트로 변환
            val singleObject = Gson().fromJson<T>(this, T::class.java)
            arrayListOf(singleObject)
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf() // 변환 실패 시 빈 리스트 반환
        }
    }
}


