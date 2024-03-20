package com.eatssu.android.ui.my_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.UserService

class MyPageViewModelFactory(private val userService: UserService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(userService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
