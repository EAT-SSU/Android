package com.eatssu.android.ui.mypage.signout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivitySignoutBinding

class SignoutActivity : BaseActivity<ActivitySignoutBinding>(ActivitySignoutBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "탈퇴하기" // 툴바 제목 설정


//        initializeViewModel()
//        setupViewModel()
//        observeViewModel()
//
//        bindData()
    }
}