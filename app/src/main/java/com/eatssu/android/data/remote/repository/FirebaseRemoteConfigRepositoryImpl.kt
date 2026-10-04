package com.eatssu.android.data.remote.repository

import com.eatssu.android.R
import com.eatssu.android.domain.model.AppTheme
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.common.enums.Restaurant
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigRepositoryImpl @Inject constructor(
    private val json: Json
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
        fetchAndActivateSafely()
        return instance.getLong("android_version_code")
    }

    override suspend fun getAppTheme(): AppTheme {
        fetchAndActivateSafely()
        return AppTheme.fromStringOrDefault(instance.getString("app_theme"))
    }

    override suspend fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo? {
        fetchAndActivateSafely()
        return getCafeteriaInfo().find { it.enum == restaurant }
    }

    private suspend fun fetchAndActivateSafely() {
        // min fetch interval이 지나지 않았으면 로컬 캐시를 사용하고, 지났으면 서버에서 가져옵니다.
        try {
            instance.fetchAndActivate().await()
        } catch (e: Exception) {
            Timber.e(e, "RemoteConfig fetchAndActivate 실패")
        }
    }

    private fun getCafeteriaInfo(): List<RestaurantInfo> {
        val jsonString = instance.getString("cafeteria_information")
        return runCatching { parseCafeteriaJson(jsonString) }
            .onFailure { Timber.e(it, "cafeteria_information JSON 파싱 실패") }
            .getOrDefault(emptyList())
    }

    private fun parseCafeteriaJson(jsonString: String): List<RestaurantInfo> {
        return try {
            json.parseToJsonElement(jsonString).jsonArray.mapNotNull { element ->
                runCatching { json.decodeFromJsonElement<RestaurantInfo>(element) }
                    .onFailure { Timber.w(it, "지원하지 않는 식당 정보 제외: $element") }
                    .getOrNull()
            }.also {
                Timber.d("Loaded cafeteria info: $it")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse cafeteria JSON")
            emptyList()
        }
    }
}
