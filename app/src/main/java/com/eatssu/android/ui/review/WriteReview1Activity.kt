package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityWriteReview1Binding

class WriteReview1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNextReview1.setOnClickListener(){
            val intent = Intent(this, WriteReview2Activity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }
    }


}