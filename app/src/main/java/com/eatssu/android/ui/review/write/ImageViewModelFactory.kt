package com.eatssu.android.ui.review.write


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.ImageService

class ImageViewModelFactory(private val imageService: ImageService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(imageService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}