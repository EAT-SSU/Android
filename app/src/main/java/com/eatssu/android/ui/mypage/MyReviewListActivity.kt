package com.eatssu.android.ui.mypage

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.model.response.GetMyReviewResponse
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.databinding.*
import com.eatssu.android.ui.review.MyReviewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//@GET("mypage/myreview") //내가 쓴 리뷰 모아보기
//fun getMyReviews(): Call<GetMyReviewResponse>

class MyReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewListBinding
    lateinit var menu :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReviewClose.setOnClickListener() {
            onBackPressed();
        }

        supportActionBar?.title = "내가 쓴 리뷰"

        var MENU_ID:Int=intent.getIntExtra("menuId",-1)
        Log.d("post",MENU_ID.toString())


        lodeReview()
    }

    private fun setAdapter(reviewList: List<GetMyReviewResponse.Data>) {
        val listAdapter = MyReviewAdapter(reviewList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvReview.adapter = listAdapter
        binding.rvReview.layoutManager = linearLayoutManager
        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

    }

    private fun lodeReview() {
        val myPageService = RetrofitImpl.retrofit.create(MyPageService::class.java)
        myPageService.getMyReviews().enqueue(object :
            Callback<GetMyReviewResponse> {
            override fun onResponse(
                call: Call<GetMyReviewResponse>,
                response: Response<GetMyReviewResponse>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString());

                    val body = response.body()
                    body?.let {
                        setAdapter(it.dataList)
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetMyReviewResponse>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}