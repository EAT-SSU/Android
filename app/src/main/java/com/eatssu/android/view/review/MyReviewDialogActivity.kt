package com.eatssu.android.view.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding

class MyReviewDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewDialogBinding
    var reviewId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyReviewDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        reviewId = intent.getLongExtra("reviewId", -1L)
    }
}