package com.eatssu.android.ui.mypage.usernamechange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.UserService

class UserNameChangeViewModelFactory(private val userService: UserService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserNameChangeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserNameChangeViewModel(userService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

