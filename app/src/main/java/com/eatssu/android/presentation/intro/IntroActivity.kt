package com.eatssu.android.presentation.intro

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.databinding.ActivityIntroBinding
import com.eatssu.android.presentation.MainActivity
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.base.ActivityCompanionWithArgs
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    @Parcelize
    data class Args(val launchPath: LaunchPath) : Parcelable
    companion object : ActivityCompanionWithArgs<Args>(IntroActivity::class, Args::class)

    private val introViewModel: IntroViewModel by viewModels()
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        log()

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

            introViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> {
                        // 에러 메시지 표시
                        showToast(event.message)
                    }
                }
            }
        }
    }

    private fun log() {
        val launchPath = intentOptions?.launchPath ?: LaunchPath.ICON
        EventLogger.appLaunch(launchPath)
    }

    override fun onResume() {
        super.onResume()
        EventLogger.screenView(ScreenId.LOGIN_SPLASH)
    }
}