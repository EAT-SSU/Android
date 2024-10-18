package com.eatssu.android.ui.common

import androidx.lifecycle.ViewModel
import com.eatssu.android.BuildConfig.VERSION_CODE
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import timber.log.Timber

class VersionViewModel(private val repository: FirebaseRemoteConfigRepository) : ViewModel() {

    init {
        // Repository 초기화
        repository.init()
    }

    fun checkForceUpdate(): Boolean {

        val versionCode = checkVersionCode() //얘가 파이어베이스에 있는 최신 버전
        val thisCheckVersionCode = VERSION_CODE

        Timber.d("앱의 versionCode는 " + thisCheckVersionCode + " 배포된 최신 버전은 " + versionCode)

        if (thisCheckVersionCode < versionCode) { //배포된 버전이 크면 강제 업데이트
            Timber.d("강제업데이트")
            return true
        } else if (thisCheckVersionCode >= versionCode) { //이 버전이 더 크거나 같으면 강제 업데이트 할 필요 x
            Timber.d("업데이트 패스~")
            return false
        }
        return false
    }


    fun checkVersionCode(): Long {
        return repository.getVersionCode()
    }

//    fun checkAndroidMessage(): AndroidMessage {
//        return repository.getAndroidMessage()
//    }
}