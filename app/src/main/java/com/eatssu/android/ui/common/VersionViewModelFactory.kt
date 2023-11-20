package com.eatssu.android.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class VersionViewModelFactory(private val repository: FirebaseRemoteConfigRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VersionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VersionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

