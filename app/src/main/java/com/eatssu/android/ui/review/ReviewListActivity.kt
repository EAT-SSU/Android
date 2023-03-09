package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityReviewListBinding

class ReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setAdapter()


        binding.btnNextReview.setOnClickListener(){
            val intent = Intent(this, WriteReview1Activity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }
    }
//
//    private fun setAdapter(reviewList: List<review>){
//
//        val reviewAdapter = ReviewAdapter(reviewList)
//        binding.rvReview.adapter=reviewAdapter
//
//        val linearLayoutManager = LinearLayoutManager(this)
//        binding.rvReview.layoutManager=linearLayoutManager
//
//        binding.rvReview.setHasFixedSize(true)
//        // 1. 정의되어 있는 구분선
//        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
//    }
}