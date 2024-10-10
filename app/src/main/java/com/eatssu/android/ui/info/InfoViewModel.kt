package com.eatssu.android.ui.info

import android.util.Log
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.RestaurantInfo
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) : ViewModel() {

    // StateFlow to hold restaurant info list
    private val _infoList = MutableStateFlow<List<RestaurantInfo>>(emptyList())
    val infoList: StateFlow<List<RestaurantInfo>> = _infoList.asStateFlow()

    // Map to hold restaurant info
    private val restaurantInfoMap: MutableMap<Restaurant, RestaurantInfo> = mutableMapOf()

    init {

        // Load cafeteria info from repository and update the StateFlow
        _infoList.value = firebaseRemoteConfigRepository.getCafeteriaInfo()
        Log.d("InfoViewModel", _infoList.value.toString())
        _infoList.value.forEach { restaurantInfo ->
            restaurantInfoMap[restaurantInfo.enum] = restaurantInfo
        }
    }

    // Helper function to get restaurant details
    fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo? {
        return restaurantInfoMap[restaurant]
    }
}
