package com.eatssu.android.ui.main.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.MenuService

class MenuViewModelFactory(private val menuService: MenuService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

