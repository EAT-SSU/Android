package com.eatssu.android.presentation.mypage

import android.os.Bundle
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityDeveloperBinding
import com.eatssu.android.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable

@AndroidEntryPoint
class DeveloperActivity :
    BaseActivity<ActivityDeveloperBinding>(ActivityDeveloperBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = "#B8E4FF".toColorInt()
        window.navigationBarColor = "#C7FFE3".toColorInt()

        toolbarTitle.text = getString(R.string.developer) // 툴바 제목 설정
        toolbar.background = "#B8E4FF".toColorInt().toDrawable()
    }
}