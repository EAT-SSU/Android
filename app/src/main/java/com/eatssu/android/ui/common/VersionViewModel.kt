package com.eatssu.android.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class VersionViewModel(firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository): ViewModel() {

    val version: MutableLiveData<String> = MutableLiveData()
    val isForceUpdate: MutableLiveData<Boolean> = MutableLiveData()


    init {
        version.value = firebaseRemoteConfigRepository.getAppVersion()
        isForceUpdate.value = firebaseRemoteConfigRepository.getForceUpdate()
    }
}