package com.eatssu.android.presentation.mypage

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivitySignOutBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignOutActivity :
    BaseActivity<ActivitySignOutBinding>(
        ActivitySignOutBinding::inflate,
        ScreenId.MYPAGE_SIGNOUT
    ) {
    //TODO 현재 dev서버 탈퇴하기 500

    private val signOutViewModel: SignOutViewModel by viewModels()

    private var inputNickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = getString(R.string.title_sign_out) // 툴바 제목 설정

        val nickname = intent.getStringExtra("nickname")?.trim() ?: ""

        binding.btnSignOut.isEnabled = false

        binding.etEnterNickname.hint = nickname
        binding.etEnterNickname.doAfterTextChanged {
            compareNickname(nickname)
        }

        setOnClickListener()

        lifecycleScope.launch {
            signOutViewModel.uiState.collectLatest {
                when (it) {
                    is UiState.Init -> {}

                    is UiState.Loading -> {
                        //로딩중
                    }

                    is UiState.Success -> {
                        if (it.data.isSignOuted) {
                            val intent = Intent(this@SignOutActivity, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }

                    is UiState.Error -> {
                        //에러
                    }
                }
            }
        }

        lifecycleScope.launch {
            signOutViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> showToast(event)
                }
            }
        }
    }


    private fun setOnClickListener() {
        binding.btnSignOut.setOnClickListener {
            signOutViewModel.signOut()
        }
    }

    private fun compareNickname(nickname: String) {
        //입력값 담기
        inputNickname = binding.etEnterNickname.text?.toString()?.trim() ?: ""
        // 값 유무에 따른 활성화 여부
        if (inputNickname == nickname) {
            binding.btnSignOut.isEnabled = true
        } else {
            binding.btnSignOut.isEnabled = false
        }
    }
}