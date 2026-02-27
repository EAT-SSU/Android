package com.eatssu.android.presentation.mypage

import android.content.Intent
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityDeveloperBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeveloperActivity :
    BaseActivity<ActivityDeveloperBinding>(
        ActivityDeveloperBinding::inflate,
        ScreenId.MYPAGE_DEVELOPER
    ) {
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
                putExtra(WebViewActivity.EXTRA_TITLE, "Who’s next?")
                putExtra(WebViewActivity.EXTRA_URL, getString(R.string.recruiting_url))
                putExtra("SCREEN_ID", ScreenId.EXTERNAL_RECRUIT.name)
            }
            startActivity(intent)
        }
    }
}
