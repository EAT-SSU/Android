package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.databinding.ActivityWriteReview1Binding
import com.eatssu.android.ui.BaseActivity

class WriteReview1Activity : BaseActivity() {
    private lateinit var binding: ActivityWriteReview1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview1Binding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_write_review1, findViewById(R.id.frame_layout), true)

        supportActionBar?.title = "리뷰남기기"


        binding.btnNextReview1.setOnClickListener(){
            val intent = Intent(this, WriteReview2Activity::class.java)  // 인텐트를 생성해줌,
            intent.putExtra("rating",binding.rbReview1.rating)
            startActivity(intent)  // 화면 전환을 시켜줌
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_write_review1
    }
}