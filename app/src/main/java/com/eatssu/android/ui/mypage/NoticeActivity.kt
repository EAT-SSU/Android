package com.eatssu.android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.BaseActivity
import com.eatssu.android.databinding.ActivityNoticeBinding
import com.eatssu.android.databinding.ActivityReportBinding

class NoticeActivity : BaseActivity() {
    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)

        setActionBarTitle("공지사항")

        setContentView(binding.root)
    }
}