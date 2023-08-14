package com.eatssu.android.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.repository.MenuRepository
import com.eatssu.android.repository.ReviewListRepository

import com.eatssu.android.viewmodel.MenuViewModel
import com.eatssu.android.viewmodel.ReviewListViewModel

class ReviewListViewModelFactory(private val repository: ReviewListRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

