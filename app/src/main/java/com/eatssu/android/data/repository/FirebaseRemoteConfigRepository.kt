package com.eatssu.android.data.repository

import android.util.Log
import com.eatssu.android.R
import com.eatssu.android.data.entity.AndroidMessage
import com.eatssu.android.data.entity.FirebaseInfoItem
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import org.json.JSONArray

class FirebaseRemoteConfigRepository {
    private val instance = FirebaseRemoteConfig.getInstance()

    fun init() {
        // Firebase Remote Config 초기화 설정
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 캐시된 값을 1시간마다 업데이트
            .build()
        instance.setConfigSettingsAsync(configSettings)
        instance.setDefaultsAsync(R.xml.firebase_remote_config)

        // Set the default values locally
        instance.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseRemoteConfigRepository", "fetchAndActivate 성공")
            } else {
                // Handle error
                Log.d("FirebaseRemoteConfigRepository", "fetchAndActivate error")
                throw RuntimeException("fetchAndActivate 실패")
            }
        }
    }

    fun getAndroidMessage(): AndroidMessage {

        val jsonString =instance.getString("android_message")

        // Gson을 사용하여 JSON 문자열을 DTO로 파싱
        val serverStatus: AndroidMessage = Gson().fromJson(jsonString, AndroidMessage::class.java)

        // 파싱된 결과 확인
        println("Dialog: ${serverStatus.dialog}")
        println("Message: ${serverStatus.message}")

        return serverStatus
    }

    fun getForceUpdate(): Boolean {
        return instance.getBoolean("force_update")
    }

    fun getAppVersion(): String {
        return instance.getString("app_version")
    }

    fun getCafeteriaInfo(): ArrayList<FirebaseInfoItem> {
        return parsingJson(instance.getString("cafeteria_info"))
    }

    private fun parsingJson(json: String): ArrayList<FirebaseInfoItem> {
        val jsonArray = JSONArray(json)
        val list = ArrayList<FirebaseInfoItem>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)

            val name = jsonObject.optString("name", "")
            val location = jsonObject.optString("location", "")
            val time = jsonObject.optString("time", "")
            val etc = jsonObject.optString("etc", "")

            val firebaseInfoItem = FirebaseInfoItem(name, location, time, etc)
            Log.d("FirebaseRemoteConfigRepository", firebaseInfoItem.toString())
            list.add(firebaseInfoItem)
        }
        return list
    }
}