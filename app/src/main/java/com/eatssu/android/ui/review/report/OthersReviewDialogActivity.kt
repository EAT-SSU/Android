package com.eatssu.android.ui.review.report

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.databinding.ActivityOthersReviewDialogBinding

class OthersReviewDialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOthersReviewDialogBinding
    var reviewId = -1L
    var menuId = -1L
    var menu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOthersReviewDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()


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