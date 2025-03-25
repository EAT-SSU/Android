package com.eatssu.android.presentation.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.presentation.main.MainActivity
import com.eatssu.android.presentation.util.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private val introViewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            introViewModel.autoLogin()

            lifecycleScope.launch {
                introViewModel.uiState.collectLatest {
                    if (it.isAutoLogined) {
                        startActivity<MainActivity>()

                        // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
                        // 이동한 다음 사용안함으로 finish 처리
                        finish()
                    } else {
                        startActivity<LoginActivity>()

                        // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
                        // 이동한 다음 사용안함으로 finish 처리
                        finish()
                    }

                }
            }

        }, 2000) // 시간 2초 이후 실행

    }
}