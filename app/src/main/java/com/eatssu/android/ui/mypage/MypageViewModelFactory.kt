package com.eatssu.android.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class MypageViewModelFactory(private val repository: FirebaseRemoteConfigRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MypageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MypageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

