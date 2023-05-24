package com.eatssu.android.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.R
import com.eatssu.android.databinding.*
import com.eatssu.android.ui.BaseActivity


class MyReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewListBinding
/*
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_my_review_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyReviewListBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_my_review_list, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)
        supportActionBar?.title = "내가 쓴 리뷰"

        val reviewList : ArrayList<review> = arrayListOf()

        reviewList.apply {
            add(review("김치볶음밥","여기 계란국 맛집임... 김치볶음밥에 계란후라이 없어서 아쉽\n" +
                    "다음에 또 먹어야지","2023-03-16"))
            add(review("나시고랭","김치볶음밥은 실패할 수 없다 배부르게 잘 먹고 감 ","2023-03-17"))
            add(review("쌀국수","여기 계란국 맛집임... 김치볶음밥에 계란후라이 없어서 아쉽\n" +
                    "다음에 또 먹어야지","2023-03-16"))
            add(review("찜닭","김치볶음밥은 실패할 수 없다 배부르게 잘 먹고 감 ","2023-03-17"))
        }

        val listAdapter = MyReviewAdapter(reviewList)
        binding.rvReview.adapter=listAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager=linearLayoutManager

        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))



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
//    }*/
}