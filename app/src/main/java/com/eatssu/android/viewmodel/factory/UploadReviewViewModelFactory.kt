package com.eatssu.android.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.repository.ReviewRepository

import com.eatssu.android.viewmodel.ReviewViewModel
import com.eatssu.android.viewmodel.UploadReviewViewModel

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

