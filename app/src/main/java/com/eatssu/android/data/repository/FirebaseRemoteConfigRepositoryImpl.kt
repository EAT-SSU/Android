package com.eatssu.android.data.repository

import com.eatssu.android.R
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.common.enums.Restaurant
import com.google.common.reflect.TypeToken
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigRepositoryImpl @Inject constructor(
) : FirebaseRemoteConfigRepository {

    private val instance = FirebaseRemoteConfig.getInstance()

    override suspend fun init(): Result<Unit> {
        return try {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(600)
                .build()
            instance.setConfigSettingsAsync(configSettings)
            instance.setDefaultsAsync(R.xml.firebase_remote_config)
            instance.fetchAndActivate().await()

            Timber.d("RemoteConfig fetchAndActivate 성공")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "RemoteConfig fetchAndActivate 실패")
            instance.setDefaultsAsync(R.xml.firebase_remote_config)
            Result.failure(e)
        }
    }

    override fun getMinimumVersionCode(): Long =
        instance.getLong("android_version_code")

    override fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo? {
        return getCafeteriaInfo().find { it.enum == restaurant }
    }

    fun getCafeteriaInfo(): List<RestaurantInfo> {
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
                    enum = Restaurant.valueOf(dto.enum.toString()),
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
