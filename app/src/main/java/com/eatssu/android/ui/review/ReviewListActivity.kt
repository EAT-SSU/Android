package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.model.review
import com.eatssu.android.databinding.ActivityReviewListBinding

class ReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reviewList : ArrayList<review> = arrayListOf()

        reviewList.apply {
            add(review("유진","여기 계란국 맛집임... 김치볶음밥에 계란후라이 없어서 아쉽\n" +
                    "다음에 또 먹어야지","2023-03-16"))
            add(review("박윤빈","김치볶음밥은 실패할 수 없다 배부르게 잘 먹고 감 ","2023-03-17"))
            add(review("유진","여기 계란국 맛집임... 김치볶음밥에 계란후라이 없어서 아쉽\n" +
                    "다음에 또 먹어야지","2023-03-16"))
            add(review("박윤빈","김치볶음밥은 실패할 수 없다 배부르게 잘 먹고 감 ","2023-03-17"))
        }

        val listAdapter = ListAdapter(reviewList)
        binding.rvReview.adapter=listAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager=linearLayoutManager

        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))



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