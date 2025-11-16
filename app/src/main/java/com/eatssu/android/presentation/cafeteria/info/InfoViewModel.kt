package com.eatssu.android.presentation.cafeteria.info

import androidx.lifecycle.ViewModel
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.common.enums.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) : ViewModel() {

    /**
     * 특정 식당 정보를 가져옵니다.
     * 필요할 때만 호출하여 메모리 효율성을 높입니다.
     * 값을 가져오기 전에 fetchAndActivate를 호출하여 최신 값을 가져옵니다.
     */
    suspend fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo? {
        return try {
            val restaurantInfo = firebaseRemoteConfigRepository.getRestaurantInfo(restaurant)
            Timber.d("Loaded restaurant info for $restaurant: $restaurantInfo")
            restaurantInfo
        } catch (e: Exception) {
            Timber.e(e, "Failed to load restaurant info for $restaurant")
            null
        }
    }
}
