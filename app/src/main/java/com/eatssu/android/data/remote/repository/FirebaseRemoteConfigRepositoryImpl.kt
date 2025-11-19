package com.eatssu.android.data.remote.repository

import com.eatssu.android.R
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.common.enums.Restaurant
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigRepositoryImpl @Inject constructor(
) : FirebaseRemoteConfigRepository {

    private val instance = FirebaseRemoteConfig.getInstance()

    init {
        // Remote Config 설정 초기화 (fetchAndActivate는 각 값 가져오기 전에 호출)
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(600)
            .build()
        instance.setConfigSettingsAsync(configSettings)
        instance.setDefaultsAsync(R.xml.firebase_remote_config)
    }

    override suspend fun getMinimumVersionCode(): Long {
        // 값을 가져오기 전에 fetchAndActivate 호출
        // min fetch interval이 지나지 않았으면 로컬 캐시를 사용하고, 지났으면 서버에서 가져옵니다.
        try {
            instance.fetchAndActivate().await()
        } catch (e: Exception) {
            Timber.e(e, "RemoteConfig fetchAndActivate 실패")
        }
        return instance.getLong("android_version_code")
    }

    override suspend fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo? {
        // 값을 가져오기 전에 fetchAndActivate 호출
        // min fetch interval이 지나지 않았으면 로컬 캐시를 사용하고, 지났으면 서버에서 가져옵니다.
        try {
            instance.fetchAndActivate().await()
        } catch (e: Exception) {
            Timber.e(e, "RemoteConfig fetchAndActivate 실패")
        }
        return getCafeteriaInfo().find { it.enum == restaurant }
    }

    private fun getCafeteriaInfo(): List<RestaurantInfo> {
        val json = instance.getString("cafeteria_information")
        return runCatching { parseCafeteriaJson(json) }
            .onFailure { Timber.e(it, "cafeteria_information JSON 파싱 실패") }
            .getOrDefault(emptyList())
    }

    private fun parseCafeteriaJson(json: String): List<RestaurantInfo> {
        return try {
            val gson = Gson()
            val listType = object : TypeToken<List<RestaurantInfo>>() {}.type
            val dtoList: List<RestaurantInfo> = gson.fromJson(json, listType)

            dtoList.map { dto ->
                RestaurantInfo(
                    enum = dto.enum,
                    name = dto.name,
                    location = dto.location,
                    image = dto.image,
                    time = dto.time,
                    etc = dto.etc
                ).also {
                    Timber.d("Loaded cafeteria info: $it")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse cafeteria JSON")
            emptyList()
        }
    }
}
