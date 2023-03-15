package com.eatssu.android.ui.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityWriteReview2Binding
class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}