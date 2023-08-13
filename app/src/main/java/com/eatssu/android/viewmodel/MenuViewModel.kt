package com.eatssu.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.repository.MenuRepository

// MenuViewModel.kt
class MenuViewModel(private val repository: MenuRepository) : ViewModel() {
//    fun getTodayMeal(restaurantType: Restaurant, time: String): LiveData<GetTodayMealResponseDto> {
//        return repository.getTodayMeal(restaurantType, time)
//    }
//
//    fun getFixedMenu(restaurantType: Restaurant): LiveData<List<GetFixedMenuResponseDto.FixMenuInfoList>> {
//        return repository.getFixedMenu(restaurantType)
//    }
}