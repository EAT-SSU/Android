package com.eatssu.android.ui.mypage

import android.os.Bundle
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityDeveloperBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeveloperActivity :
    BaseActivity<ActivityDeveloperBinding>(ActivityDeveloperBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarTitle.text = getString(R.string.developer) // 툴바 제목 설정
    }
}