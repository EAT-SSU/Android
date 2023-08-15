package com.eatssu.android.view.mypage

import android.os.Bundle
import com.eatssu.android.databinding.ActivityNoticeBinding
import com.eatssu.android.base.BaseActivity

abstract class NoticeActivity : BaseActivity() {
    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)

        setActionBarTitle("공지사항")

        setContentView(binding.root)
    }

    abstract fun setActionBarTitle(s: String)
}
