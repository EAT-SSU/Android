package com.eatssu.android.presentation.info

import androidx.lifecycle.ViewModel
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.model.RestaurantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) : ViewModel() {

    private val _infoList = MutableStateFlow<List<RestaurantInfo>>(emptyList())
    val infoList: StateFlow<List<RestaurantInfo>> = _infoList.asStateFlow()

    private val restaurantInfoMap: MutableMap<Restaurant, RestaurantInfo> = mutableMapOf()

    init {
        _infoList.value = firebaseRemoteConfigRepository.getCafeteriaInfo()
        Timber.d(_infoList.value.toString())
        _infoList.value.forEach { restaurantInfo ->
            restaurantInfoMap[restaurantInfo.enum] = restaurantInfo
        }
    }

    fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo? {
        return restaurantInfoMap[restaurant]
    }
}
