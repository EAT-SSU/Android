package com.eatssu.android.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.MyPageService

class MypageViewModelFactory(private val myPageService: MyPageService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MypageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MypageViewModel(myPageService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

