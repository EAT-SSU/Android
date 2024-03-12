package com.eatssu.android.ui.mypage.myreview

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityMyReviewListBinding
import com.eatssu.android.util.RetrofitImpl.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyReviewListActivity : BaseActivity<ActivityMyReviewListBinding>(ActivityMyReviewListBinding::inflate) {
    lateinit var menu :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내가 쓴 리뷰" // 툴바 제목 설정

        lodeReview()
    }

    private fun setAdapter(reviewList: List<MyReviewResponse.Data>) {
        val listAdapter = MyReviewAdapter(reviewList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvReview.adapter = listAdapter
        binding.rvReview.layoutManager = linearLayoutManager
        binding.rvReview.setHasFixedSize(true)
    }

    private fun lodeReview() {
        val userService = retrofit.create(UserService::class.java)
        userService.getMyReviews().enqueue(object :
            Callback<BaseResponse<MyReviewResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<MyReviewResponse>>,
                response: Response<BaseResponse<MyReviewResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()

                    if(body?.result?.dataList?.size==0){
                        binding.llNonReview.visibility=View.VISIBLE
                        binding.nestedScrollView.visibility=View.GONE
                    }else{
                        binding.llNonReview.visibility=View.GONE
                        binding.nestedScrollView.visibility=View.VISIBLE
                        setAdapter(body!!.result!!.dataList)
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BaseResponse<MyReviewResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
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