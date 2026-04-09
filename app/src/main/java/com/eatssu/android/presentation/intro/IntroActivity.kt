package com.eatssu.android.presentation.intro

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.BuildConfig
import com.eatssu.android.databinding.ActivityIntroBinding
import com.eatssu.android.domain.model.AppTheme
import com.eatssu.android.presentation.MainActivity
import com.eatssu.android.presentation.common.ForceUpdateDialogActivity
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.util.observeNetworkError
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.AppAnalyticsEvent
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val introViewModel: IntroViewModel by viewModels()
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        log()

        // 로컬에 저장된 테마 동기로 불러오기
        applySplashTheme(introViewModel.appTheme.value)
        observeTheme()

        observeState()
        observeEvents()
        observeNetworkError()

        lifecycleScope.launch {
            // 버전 체크 결과 관찰
            introViewModel.versionCheckResult.collectLatest { result ->
                result?.let {
                    when (it) {
                        is VersionCheckResult.ForceUpdateRequired -> {
                            showForceUpdateDialog()
                        }

                        VersionCheckResult.UpdateNotRequired -> {
                            // 업데이트 불필요 - 정상 진행
                        }
                    }
                }
            }
        }
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            introViewModel.appTheme.collectLatest { theme ->
                applySplashTheme(theme)
                syncLauncherIcon(theme)
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            introViewModel.uiState.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        startActivity<MainActivity>()
                        finish()
                    }

                    is UiState.Error -> {
                        // 로그인 액티비티로 이동
                        startActivity<LoginActivity>()
                        finish()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun applySplashTheme(theme: AppTheme) {
        binding.root.setBackgroundResource(theme.splashBackgroundResId)
        binding.ivLogo.setImageResource(theme.splashLogoResId)
    }

    private fun syncLauncherIcon(theme: AppTheme) {
        if (BuildConfig.DEBUG) return

        lifecycleScope.launch(Dispatchers.IO) {
            val currentPackageName = this@IntroActivity.packageName
            val targetAliasName = "$currentPackageName${theme.launcherAliasSuffix}"

            AppTheme.entries.forEach { appTheme ->
                val aliasName = "$currentPackageName${appTheme.launcherAliasSuffix}"
                val componentName = ComponentName(currentPackageName, aliasName)
                val newState = if (aliasName == targetAliasName) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }

                // 현재 상태와 다를 때만 설정 변경하여 불필요한 IPC 호출 방지
                if (packageManager.getComponentEnabledSetting(componentName) != newState) {
                    packageManager.setComponentEnabledSetting(
                        componentName,
                        newState,
                        PackageManager.DONT_KILL_APP,
                    )
                }
            }
            Timber.d("런처 아이콘 테마 적용 완료: %s", theme.remoteValue)
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            introViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> {
                        // 에러 메시지 표시
                        showToast(event)
                    }
                }
            }
        }
    }

    private fun log() {
        val launchPath = intent.getStringExtra("launch_path")
        when (launchPath) {
            "widget" -> analyticsTracker.track(AppAnalyticsEvent.Launch(LaunchPath.WIDGET))
            "local_notification" -> analyticsTracker.track(AppAnalyticsEvent.Launch(LaunchPath.LOCAL_NOTIFICATION))
            "remote_notification" -> analyticsTracker.track(AppAnalyticsEvent.Launch(LaunchPath.REMOTE_NOTIFICATION))
            // launch_path가 없으면 일반적인 앱 아이콘 클릭으로 간주
            else -> analyticsTracker.track(AppAnalyticsEvent.Launch(LaunchPath.ICON))
        }
    }

    override fun onResume() {
        super.onResume()
        analyticsTracker.track(ScreenViewEvent(ScreenId.LOGIN_SPLASH))
    }

    private fun showForceUpdateDialog() {
        val intent = Intent(this, ForceUpdateDialogActivity::class.java)
        startActivity(intent)
    }
}
