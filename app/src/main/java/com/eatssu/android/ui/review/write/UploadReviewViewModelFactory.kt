package com.eatssu.android.ui.review.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.repository.ReviewRepository

class UploadReviewViewModelFactory(private val repository: ReviewRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

