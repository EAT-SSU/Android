package com.eatssu.android.domain.usecase.version

import com.eatssu.android.BuildConfig
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Remote Config에서 최신 값을 가져와서
 * 현재 앱 버전과 비교하여 강제 업데이트가 필요한지 판단합니다.
 *
 */
class CheckForceUpdateRequiredUseCase @Inject constructor(
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) {
    /**
     * @return 현재 앱 버전이 Firebase에 설정된 최소 버전보다 낮으면 true (업데이트 필요)
     */
    suspend operator fun invoke(): Boolean {
        val remoteVersionCode = firebaseRemoteConfigRepository.getVersionCode()
        val currentVersionCode = BuildConfig.VERSION_CODE

        Timber.d("현재 앱 버전: $currentVersionCode, Firebase 최소 버전: $remoteVersionCode")

        return currentVersionCode < remoteVersionCode
    }
}
