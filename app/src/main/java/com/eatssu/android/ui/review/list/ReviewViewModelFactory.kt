package com.eatssu.android.ui.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.repository.ReviewRepository

class ReviewViewModelFactory(private val repository: ReviewRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

