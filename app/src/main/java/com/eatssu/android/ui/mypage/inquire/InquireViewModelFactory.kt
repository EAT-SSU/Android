package com.eatssu.android.ui.mypage.inquire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.InquiresService

class InquireViewModelFactory(private val inquiresService: InquiresService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InquireViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InquireViewModel(inquiresService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

