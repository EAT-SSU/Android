package com.eatssu.android.presentation.mypage

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityDeveloperBinding
import com.eatssu.android.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable
import com.eatssu.android.presentation.mypage.terms.WebViewActivity

@AndroidEntryPoint
class DeveloperActivity :
    BaseActivity<ActivityDeveloperBinding>(ActivityDeveloperBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = "#B8E4FF".toColorInt()
        window.navigationBarColor = "#C7FFE3".toColorInt()

        toolbarTitle.text = getString(R.string.developer) // 툴바 제목 설정
        toolbar.background = "#B8E4FF".toColorInt().toDrawable()

        clickRecruiting()
    }

    private fun clickRecruiting() {
        binding.imgRecruitingBanner.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java).apply {
                putExtra("URL", getString(R.string.recruiting_url))
            }
            startActivity(intent)
        }
    }
}