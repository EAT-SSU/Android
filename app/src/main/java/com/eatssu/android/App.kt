package com.eatssu.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.eatssu.android.domain.model.TokenState
import com.eatssu.android.domain.model.TokenStateManager
import com.eatssu.android.presentation.base.TokenEventBus
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/** App: 앱이 살아있는 동안 공통 리소스 관리를 위한 클래스 */
@HiltAndroidApp
class App : Application(), Configuration.Provider {

    /** 앱 전체에서 사용할 수 있는 CoroutineScope(독립적인 공간을 만들어 안정성 높임)
     *  자식 CoroutineScope가 취소되더라도 부모 CoroutineScope는 취소되지 않음
     * */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        KakaoSdk.init(this,BuildConfig.KAKAO_NATIVE_APP_KEY)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            Firebase.analytics.setAnalyticsCollectionEnabled(false)
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            Firebase.analytics.setAnalyticsCollectionEnabled(true)
        }

        collectTokenState()
    }

    /** 토큰 상태를 application에서 감지하여 TokenEventBus에 전달 */
    private fun collectTokenState(){
        appScope.launch {
            TokenStateManager.state.collect { state ->
                if (state == TokenState.EXPIRED) {
                    TokenEventBus.notifyTokenExpired()
                } else if(state == TokenState.ERROR) {
                    TokenEventBus.notifyServerError()
                }
            }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}