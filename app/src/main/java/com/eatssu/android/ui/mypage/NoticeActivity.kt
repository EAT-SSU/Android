package com.eatssu.android.ui.mypage

import android.os.Bundle
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityNoticeBinding

abstract class NoticeActivity : BaseActivity<ActivityNoticeBinding>(ActivityNoticeBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "공지사항" // 툴바 제목 설정
    }
}
