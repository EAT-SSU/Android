package com.eatssu.android.data.repository

import com.eatssu.android.R
import com.eatssu.android.data.model.AndroidMessage
import com.eatssu.android.data.model.RestaurantInfo
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import org.json.JSONArray
import timber.log.Timber

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
                Timber.d("fetchAndActivate 성공")
            } else {
                // Handle error
                Timber.d("fetchAndActivate error")
                instance.setDefaultsAsync(R.xml.firebase_remote_config)
//                throw RuntimeException("fetchAndActivate 실패")
            }
        }
    }

    fun getAndroidMessage(): AndroidMessage {

        // Gson을 사용하여 JSON 문자열을 DTO로 파싱
        val serverStatus: AndroidMessage = Gson().fromJson(instance.getString("android_message"), AndroidMessage::class.java)

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

    fun getVersionCode(): Long {
        return instance.getLong("android_version_code")
    }

    fun getCafeteriaInfo(): ArrayList<RestaurantInfo> {
        return parsingJson(instance.getString("cafeteria_info"))
    }

    private fun parsingJson(json: String): ArrayList<RestaurantInfo> {
        val jsonArray = JSONArray(json)
        val list = ArrayList<RestaurantInfo>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)

            val name = jsonObject.optString("name", "")
            val location = jsonObject.optString("location", "")
            val time = jsonObject.optString("time", "")
            val etc = jsonObject.optString("etc", "")

            val restaurantInfo = RestaurantInfo(name, location, time, etc)
            Timber.d(restaurantInfo.toString())
            list.add(restaurantInfo)
        }
        return list
    }
}