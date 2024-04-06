package com.eatssu.android.ui.mypage.inquire

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityInquireBinding
import com.eatssu.android.util.MySharedPreferences
import com.eatssu.android.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class InquiryActivity : BaseActivity<ActivityInquireBinding>(ActivityInquireBinding::inflate) {

    private val inquireViewModel: InquireViewModel by viewModels()

    private var email = ""
    private var comment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "문의하기" // 툴바 제목 설정

        bindData()
        setOnClickListener()
    }

    private fun bindData() {
        // 카카오 로그인으로 받아온 이메일 정보 가져오기
        email = MySharedPreferences.getUserEmail(this)
        // EditText에 이메일 정보 설정
        binding.etEmail.setText(email)
        // EditText를 편집 가능하게 만들기
        binding.etEmail.isEnabled = true

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 사용자가 입력한 새로운 이메일 값을 업데이트
                email = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이 메소드는 필요하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 이 메소드는 필요하지 않음
            }
        })

        binding.etReportComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 사용자가 입력한 새로운 이메일 값을 업데이트
                comment = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이 메소드는 필요하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 이 메소드는 필요하지 않음
            }
        })//Todo 데이터 바인딩으로 리팩토링
    }

    private fun setOnClickListener() {
        binding.btnSendReport.setOnClickListener {

            val currentEmail = binding.etEmail.text.toString()
            val reportComment = binding.etReportComment.text.toString()


            Timber.d(currentEmail + reportComment)
            inquireViewModel.inquireContent(currentEmail, reportComment)

            lifecycleScope.launch {
                inquireViewModel.uiState.collectLatest {
                    if (it.done) {
                        showToast(it.toastMessage)//Todo 토스트 두번 나옴
                        finish()
                    } else {
                        showToast(it.toastMessage)
                    }
                }
            }
        }
    }
}