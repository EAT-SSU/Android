package com.eatssu.android.ui.my_page.inquire

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.InquiresService
import com.eatssu.android.databinding.ActivityInquireBinding
import com.eatssu.android.util.MySharedPreferences
import com.eatssu.android.util.RetrofitImpl
import com.eatssu.android.util.extension.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InquireActivity : BaseActivity<ActivityInquireBinding>(ActivityInquireBinding::inflate) {
    private lateinit var inquiresService: InquiresService
    private lateinit var inquireViewModel: InquireViewModel

    private var content = ""
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "문의하기" // 툴바 제목 설정

        initViewModel()
        bindData()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initViewModel() {
        inquiresService = RetrofitImpl.retrofit.create(InquiresService::class.java);
        inquireViewModel = ViewModelProvider(
            this,
            InquireViewModelFactory(inquiresService)
        )[InquireViewModel::class.java]
    }

    private fun bindData() {

        content = binding.etReportComment.text.toString()

        // 카카오 로그인으로 받아온 이메일 정보 가져오기
        val userEmail = MySharedPreferences.getUserEmail(this)

        // EditText에 이메일 정보 설정
        binding.etEmail.setText(userEmail)
        // EditText를 편집 가능하게 만들기
        binding.etEmail.isEnabled = true


    }

    private fun setOnClickListener() {
        binding.btnSendReport.setOnClickListener {
            inquireViewModel.inquireContent(content)

            lifecycleScope.launch {
                inquireViewModel.uiState.collectLatest {
                    if (!it.error && !it.loading) {
                        showToast(it.toastMessage)
                        finish()
                    }
                    if (it.error) {
                        showToast(it.toastMessage)
                    }
                }
            }
        }
    }
}