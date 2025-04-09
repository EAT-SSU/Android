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
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            introViewModel.uiState.collectLatest { state ->
                when (state) {
                    is IntroUiState.Loading -> {
                        // 할게 없는뎅? 그냥 뷰 보여주기
                    }

                    is IntroUiState.Success -> {
                        // 메인 액티비티로 이동
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