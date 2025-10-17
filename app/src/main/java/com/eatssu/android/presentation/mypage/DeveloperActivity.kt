package com.eatssu.android.presentation.mypage

import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityDeveloperBinding
import com.eatssu.android.presentation.base.ActivityCompanion
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

    companion object : ActivityCompanion(DeveloperActivity::class)
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
            WebViewActivity.start(
                this,
                WebViewActivity.Args(
                    url = getString(R.string.recruiting_url),
                    title = "Who's next?",
                    screenId = ScreenId.EXTERNAL_RECRUIT
                )
            )
        }
    }
}