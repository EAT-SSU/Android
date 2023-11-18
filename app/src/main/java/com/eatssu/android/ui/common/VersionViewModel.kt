package com.eatssu.android.ui.common

import androidx.lifecycle.ViewModel
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class VersionViewModel(private val repository: FirebaseRemoteConfigRepository) : ViewModel() {

    init {
        // Repository 초기화
        repository.init()
    }

    fun checkForceUpdate(): Boolean {
        return repository.getForceUpdate()
    }

    fun getAppVersion(): String {
        return repository.getAppVersion()
    }
}