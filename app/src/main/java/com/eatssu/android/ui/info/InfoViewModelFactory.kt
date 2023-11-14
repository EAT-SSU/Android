package com.eatssu.android.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class InfoViewModelFactory(private val repository: FirebaseRemoteConfigRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

