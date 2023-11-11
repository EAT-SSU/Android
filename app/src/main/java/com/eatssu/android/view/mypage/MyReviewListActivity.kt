package com.eatssu.android.view.mypage

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.RetrofitImpl.retrofit
import com.eatssu.android.data.model.response.GetMyReviewResponseDto
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.databinding.ActivityMyReviewListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyReviewListActivity : BaseActivity<ActivityMyReviewListBinding>(ActivityMyReviewListBinding::inflate) {
    lateinit var menu :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내가 쓴 리뷰" // 툴바 제목 설정

        val MENU_ID:Int=intent.getIntExtra("menuId",-1)
        Log.d("post",MENU_ID.toString())

        lodeReview()
    }

    private fun setAdapter(reviewList: List<GetMyReviewResponseDto.Data>) {
        val listAdapter = MyReviewAdapter(reviewList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvReview.adapter = listAdapter
        binding.rvReview.layoutManager = linearLayoutManager
        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

    }

    private fun lodeReview() {
        val myPageService = retrofit.create(MyPageService::class.java)
        myPageService.getMyReviews().enqueue(object :
            Callback<GetMyReviewResponseDto> {
            override fun onResponse(
                call: Call<GetMyReviewResponseDto>,
                response: Response<GetMyReviewResponseDto>
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

            override fun onFailure(call: Call<GetMyReviewResponseDto>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        lodeReview()
    }

    override fun onResume() {
        super.onResume()
        lodeReview()
    }
}