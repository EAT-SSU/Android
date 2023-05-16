package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.R
import com.eatssu.android.RestaurantType
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import com.eatssu.android.data.model.response.GetReviewInfoResponse
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.model.review
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.databinding.ActivityReviewListBinding
import com.eatssu.android.ui.BaseActivity
import com.eatssu.android.ui.main.MenuAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewListActivity : BaseActivity() {
    private lateinit var binding: ActivityReviewListBinding

//    private val menuId =intent.getIntExtra("menuId",35)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewListBinding.inflate(layoutInflater)
//        binding = ActivityReviewListBinding.inflate(layoutInflater, null, true)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_review_list, findViewById(R.id.frame_layout), true)

        supportActionBar?.title = "리뷰"

        lodeReviewInfo()
        lodeData()

        binding.btnNextReview.setOnClickListener() {
            val intent = Intent(this, WriteReview1Activity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
        }
    }

    private fun setAdapter(reviewList: List<GetReviewListResponse.Data>) {
        val listAdapter = ReviewAdapter(reviewList)
        binding.rvReview.adapter = listAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = linearLayoutManager

        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

    }

    private fun lodeReviewInfo() {
        val reviewService = RetrofitImpl.getApiClient().create(ReviewService::class.java)
        reviewService.reviewInfo(menuId = 35).enqueue(object :
            Callback<GetReviewInfoResponse> {
            override fun onResponse(
                call: Call<GetReviewInfoResponse>,
                response: Response<GetReviewInfoResponse>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString());
                    //Toast.makeText(this@ProfileActivity, "비밀번호 찾기 성공!", Toast.LENGTH_SHORT).show()

                    binding.tvMenu.text = response.body()?.menuName.toString()
                    binding.tvRate.text = response.body()?.grade.toString()
                    binding.tvReviewNumCount.text = response.body()?.totalReviewCount.toString()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<GetReviewInfoResponse>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    private fun lodeData() {
        val reviewService = RetrofitImpl.getApiClient().create(ReviewService::class.java)
        reviewService.getReview(menuId = 35).enqueue(object :
            Callback<GetReviewListResponse> {
            override fun onResponse(
                call: Call<GetReviewListResponse>,
                response: Response<GetReviewListResponse>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString());
                    //Toast.makeText(this@ProfileActivity, "비밀번호 찾기 성공!", Toast.LENGTH_SHORT).show()

                    val body = response.body()
                    body?.let {
                        setAdapter(it.dataList)
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<GetReviewListResponse>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_review_list
    }
}