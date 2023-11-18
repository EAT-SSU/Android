package com.eatssu.android.ui.mypage.usernamechange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.repository.UserRepositoryImpl

class UserNameChangeViewModelFactory(private val repository: UserRepositoryImpl) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserNameChangeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserNameChangeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

