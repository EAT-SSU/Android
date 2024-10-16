package com.eatssu.android.presentation.main.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.domain.service.MealService
import com.eatssu.android.domain.service.MenuService

class MenuViewModelFactory(
    private val menuService: MenuService,
    private val mealService: MealService,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuService, mealService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

