package com.eatssu.android.ui.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.databinding.ActivityReviewListBinding
import com.eatssu.android.ui.BaseActivity

abstract class ReportActivity : BaseActivity() {
    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)

        setActionBarTitle("신고하기")

        setContentView(binding.root)
    }

    abstract fun setActionBarTitle(s: String)
}