package com.eatssu.android.data.repository

import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.response.ChangeMenuInfoListDto
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import retrofit2.Call


class MenuRepository(private val menuService: MenuService) {

    fun getTodayMeal(menuDate: String, restaurant: Restaurant, time: Time): Call<GetTodayMealResponseDto> {
        return menuService.getTodayMeal(menuDate, restaurant.toString(), time.toString())
    }

    fun getFixedMenu(restaurant: Restaurant): Call<GetFixedMenuResponseDto> {
        return menuService.getFixMenu(restaurant.toString())
    }

    fun getMenuIdByMealId(mealId: Long): Call<ChangeMenuInfoListDto> {
        return menuService.getMenuByMealId(mealId)
    }
}