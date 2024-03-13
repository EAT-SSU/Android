package com.eatssu.android.ui.review.write.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.MealService

class VariableMenuModelFactory(private val mealService: MealService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VariableMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VariableMenuViewModel(mealService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

