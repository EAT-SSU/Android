package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityWriteReview2Binding
class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rbReview2.rating=intent.getFloatExtra("rating", 0F)

        binding.btnNextReview2.setOnClickListener(){
            val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }
    }

}