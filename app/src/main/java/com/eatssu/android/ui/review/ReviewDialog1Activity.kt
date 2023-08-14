package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityReviewDialog1Binding

class ReviewDialog1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewDialog1Binding
    var reviewId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewDialog1Binding.inflate(layoutInflater)

        setContentView(binding.root)

        reviewId = intent.getLongExtra("reviewId", -1L)

        binding.btnReviewReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra(
                "reviewId", reviewId
            )
            Log.d("dialogid", reviewId.toString())
            startActivity(intent)
            finish()
        }


    }
}