package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.domain.repository.MenuRepository
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import javax.inject.Inject

class GetMenuListUseCase @Inject constructor(
    private val menuRepository: MenuRepository,
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        restaurant: Restaurant,
        menuDate: String,
        time: Time
    ): List<Menu> {
        return when (restaurant.menuType) {
            MenuType.FIXED -> menuRepository.getFixedMenuList(restaurant)
            MenuType.VARIABLE -> mealRepository.getTodayMenuList(menuDate, restaurant, time)
        }
    }
}
