package com.eatssu.android.data.repository

import com.eatssu.android.R
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.common.enums.Restaurant
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import timber.log.Timber
import kotlin.coroutines.resume

class FirebaseRemoteConfigRepository {

    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        /**
         * Firebase Remote Config 초기화 설정
         *
         * 캐시된 값을 1시간(3600)마다 업데이트 -> 10분(600)
         *
         * 변경 사유: 사용자가 앱에 머무는 시간이 되게 짦다.
         */

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(600)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.firebase_remote_config)
    }

    suspend fun getVersionCode() = useFirebaseConfig {
        getLong("android_version_code")
    }

    suspend fun getCafeteriaInfo() = useFirebaseConfig {
        parsingJson(getString("cafeteria_information"))
    }

    private suspend fun <T> useFirebaseConfig(block: FirebaseRemoteConfig.() -> T): T {
        fetchAndActivateSuspend()
        // fetchAndActivate가 완료된 후에 새로운 값을 가져오도록 보장
        return block(FirebaseRemoteConfig.getInstance())
    }

    private suspend fun fetchAndActivateSuspend() = suspendCancellableCoroutine { continuation ->
        // fetchAndActivate는 minimumFetchIntervalInSeconds 보다 짧은 시간에 여러번 호출되어도
        // 실제로는 minimumFetchIntervalInSeconds 이후에만 fetch가 수행된다.

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            continuation.resume(Unit)
        }
    }

    private fun parsingJson(json: String): ArrayList<RestaurantInfo> {
        val jsonArray = JSONArray(json)
        val list = ArrayList<RestaurantInfo>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)

            val enumString = jsonObject.optString("enum", "")
            val enumValue =
                enumValues<Restaurant>().find { it.name == enumString } ?: Restaurant.HAKSIK
            val name = jsonObject.optString("name", "")
            val location = jsonObject.optString("location", "")
            val photoUrl = jsonObject.optString("image", "")
            val time = jsonObject.optString("time", "")
            val etc = jsonObject.optString("etc", "")

            val restaurantInfo = RestaurantInfo(enumValue, name, location, photoUrl, time, etc)
            Timber.d(restaurantInfo.toString())
            list.add(restaurantInfo)
        }
        return list
    }
}