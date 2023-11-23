package com.eatssu.android.ui.mypage.usernamechange

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityUserNameChangeBinding
import com.eatssu.android.util.RetrofitImpl.retrofit

class UserNameChangeActivity : BaseActivity<ActivityUserNameChangeBinding>(ActivityUserNameChangeBinding::inflate) {
    private var inputNickname: String = ""
    private lateinit var viewModel: UserNameChangeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "닉네임 설정" // 툴바 제목 설정

        binding.btnCheckNickname.isEnabled=false
        binding.btnComplete.isEnabled=false

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                inputNickname = binding.etChNickname.text.toString()
                //값 유무에 따른 활성화 여부
                if (binding.etChNickname.text != null) {
                    binding.btnCheckNickname.isEnabled =true //있다면 true 없으면 false
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        val userService = retrofit.create(UserService::class.java)

        viewModel = ViewModelProvider(
            this,
            UserNameChangeViewModelFactory(userService)
        )[UserNameChangeViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnCheckNickname.setOnClickListener {
            val inputNickname = inputNickname // Replace with your actual input
            viewModel.checkNickname(inputNickname)
        }

        binding.btnComplete.setOnClickListener {
            viewModel.changeNickname(inputNickname)
            viewModel.isDone.observe(this){ isDone ->
                if(isDone){ finish() }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isEnableNickname.observe(this) { isEnableNickname ->
            binding.btnComplete.isEnabled = isEnableNickname
        }

        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}