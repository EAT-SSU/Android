package com.eatssu.android.ui.common

import androidx.lifecycle.ViewModel
import com.eatssu.android.BuildConfig.VERSION_NAME
import com.eatssu.android.data.entity.AndroidMessage
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class VersionViewModel(private val repository: FirebaseRemoteConfigRepository) : ViewModel() {

    init {
        // Repository 초기화
        repository.init()
    }

    fun checkForceUpdate(): Boolean {
        val lastVersion = checkAppVersion()
        return VERSION_NAME != lastVersion && repository.getForceUpdate()
    }

    private fun checkAppVersion(): String {
        return repository.getAppVersion()
    }

    fun checkAndroidMessage(): AndroidMessage {
        return repository.getAndroidMessage()
    }
}