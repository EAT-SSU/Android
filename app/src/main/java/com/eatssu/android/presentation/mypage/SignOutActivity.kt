package com.eatssu.android.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.databinding.ActivitySignOutBinding
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignOutActivity :
    BaseActivity<ActivitySignOutBinding>(ActivitySignOutBinding::inflate) {

    private val signOutViewModel: SignOutViewModel by viewModels()

    private var inputNickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "탈퇴하기" // 툴바 제목 설정

        val nickname = intent.getStringExtra("nickname")?.trim() ?: ""

        binding.btnSignOut.isEnabled = false

        binding.etEnterNickname.hint = nickname
        binding.etEnterNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                compareNickname(nickname)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        setOnClickListener()
    }


    private fun setOnClickListener() {
        binding.btnSignOut.setOnClickListener {
            signOutViewModel.signOut()

            lifecycleScope.launch {
                signOutViewModel.uiState.collectLatest {
                    when (it) {
                        is UiState.Init -> {}

                        is UiState.Loading -> {
                            //로딩중
                        }

                        is UiState.Success -> {
                            if (it.data?.isSignOuted == true) {
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

                signOutViewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is UiEvent.ShowToast -> {
                            showToast(event.message)
                        }
                    }
                }
            }
        }
    }

    fun compareNickname(nickname: String) {
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