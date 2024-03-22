package com.eatssu.android.ui.mypage.usernamechange

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityUserNameChangeBinding
import com.eatssu.android.util.extension.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserNameChangeActivity : BaseActivity<ActivityUserNameChangeBinding>(ActivityUserNameChangeBinding::inflate) {

    private val userNameChangeViewModel: UserNameChangeViewModel by viewModels()

    private var inputNickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "닉네임 설정" // 툴바 제목 설정

        binding.btnCheckNickname.isEnabled = false
        binding.btnComplete.isEnabled = false

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                inputNickname = binding.etChNickname.text.trim().toString()
                // 값 유무에 따른 활성화 여부
                if (binding.etChNickname.text != null) {
                    val nicknameLength = inputNickname.length
                    binding.btnCheckNickname.isEnabled = nicknameLength in 2..8
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnCheckNickname.setOnClickListener {
            userNameChangeViewModel.checkNickname(inputNickname)

            lifecycleScope.launch {
                userNameChangeViewModel.uiState.collectLatest {
                    if (it.isEnableName) {
                        binding.btnComplete.isEnabled = true
                        showToast(it.toastMessage)
                    }
                }
            }

        }

        binding.btnComplete.setOnClickListener {
            userNameChangeViewModel.changeNickname(inputNickname)

            lifecycleScope.launch {
                userNameChangeViewModel.uiState.collectLatest {
                    if (it.isDone) {
                        showToast(it.toastMessage)
                        finish()
                    }
                }
            }
        }
    }
}