package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.BaseActivity
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityWriteReview1Binding

class WriteReview1Activity : BaseActivity() {
    private lateinit var binding: ActivityWriteReview1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview1Binding.inflate(layoutInflater)

        setActionBarTitle("리뷰 남기기")

        setContentView(binding.root)

        binding.btnNextReview1.setOnClickListener(){
            val intent = Intent(this, WriteReview2Activity::class.java)  // 인텐트를 생성해줌,
            intent.putExtra("rating",binding.rbReview1.rating)
            startActivity(intent)  // 화면 전환을 시켜줌
        }
    }
}