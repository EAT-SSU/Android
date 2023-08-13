package com.eatssu.android.ui.review

import android.content.Intent
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

        binding.btnCloseReport.setOnClickListener() {
            val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌
            startActivity(intent)  // 화면 전환을 시켜줌

        }

        binding.radioGp.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                //binding.radioBt1 ->
            }
        }

        binding.btnNextReview2.setOnClickListener() {
            val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌
            startActivity(intent)  // 화면 전환을 시켜줌

        }
    }

    abstract fun setActionBarTitle(s: String)
}