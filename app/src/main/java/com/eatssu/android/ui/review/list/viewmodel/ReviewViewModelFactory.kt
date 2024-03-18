package com.eatssu.android.ui.review.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.ReviewService

class ReviewViewModelFactory(
    private val reviewService: ReviewService,
//    private val repository: ReviewRepository
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(reviewService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

