package com.eatssu.android.ui.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivitySignOutBinding
import com.eatssu.android.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignOutActivity :
    BaseActivity<ActivitySignOutBinding>(ActivitySignOutBinding::inflate) {
    //TODO 현재 dev서버 탈퇴하기 500

    private val signOutViewModel: SignOutViewModel by viewModels()

    private var inputNickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "탈퇴하기" // 툴바 제목 설정

        val nickname = intent.getStringExtra("nickname")

        binding.btnSignOut.isEnabled = false

//        binding.etEnterNickname.hint =
        binding.etEnterNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (nickname != null) {
                    checkNickname(nickname)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        setOnClickListener()
    }


    private fun setOnClickListener() {
        binding.btnSignOut.setOnClickListener {
            signOutViewModel.signOut()

            lifecycleScope.launch {
                signOutViewModel.uiState.collectLatest {
                    if (it.isSignOuted) {
                        showToast(it.toastMessage) //Todo 사용가능 토스트가 무슨 3번이나 나옴
                    } else {
                        showToast(it.toastMessage) //Todo 사용가능 토스트가 무슨 3번이나 나옴
                    }
                }
            }
        }
    }

    fun checkNickname(nickname: String) {
        //입력값 담기
        inputNickname = binding.etEnterNickname.text.trim().toString()
        // 값 유무에 따른 활성화 여부
        if (inputNickname == nickname) {
            binding.btnSignOut.isEnabled = true
        } else {
            binding.btnSignOut.isEnabled = false
        }
    }
}