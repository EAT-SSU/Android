package com.eatssu.android.presentation.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.databinding.ActivityIntroBinding
import com.eatssu.android.presentation.main.MainActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private val introViewModel: IntroViewModel by viewModels()
    private lateinit var binding: ActivityIntroBinding

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
                        finish()
                    }

                    is IntroUiState.NoValidToken -> {
                        // 로그인 액티비티로 이동
                        startActivity<LoginActivity>()
                        finish()
                    }
                }
            }

            introViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is IntroUiEvent.ShowToast -> {
                        // 에러 메시지 표시
                        showToast(event.error)
                    }
                }
            }
        }
    }
}