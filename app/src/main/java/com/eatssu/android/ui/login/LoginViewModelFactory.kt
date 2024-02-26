package com.eatssu.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.service.OauthService

class LoginViewModelFactory(private val oauthService: OauthService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(oauthService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

