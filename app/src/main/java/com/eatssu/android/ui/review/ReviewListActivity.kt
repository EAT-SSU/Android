package com.eatssu.android.ui.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class ReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewListBinding
    lateinit var menu :String

    private var MENU_ID by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.included.actionBar.text="리뷰"

        //supportActionBar?.title = "리뷰"

        MENU_ID = intent.getLongExtra("menuId",-1)
        Log.d("post", "menuID:$MENU_ID")


        lodeReviewInfo(MENU_ID)
        lodeData(MENU_ID)

        binding.btnNextReview.setOnClickListener() {
            val intent = Intent(this, WriteReviewActivity::class.java)  // 인텐트를 생성해줌,
            intent.putExtra("menuId",MENU_ID)
            intent.putExtra("menu",menu)
            startActivity(intent)  // 화면 전환을 시켜줌
        }
    }

    private fun setAdapter(reviewList: List<GetReviewListResponse.Data>?) {
        val listAdapter = reviewList?.let { ReviewAdapter(it) }
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvReview.adapter = listAdapter
        binding.rvReview.layoutManager = linearLayoutManager
        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

    }

    private fun lodeReviewInfo(id: Long) {
        val reviewService = RetrofitImpl.retrofit.create(ReviewService::class.java)
        reviewService.reviewInfo("FIX",id).enqueue(object :
            Callback<GetReviewInfoResponseDto> {
            override fun onResponse(
                call: Call<GetReviewInfoResponseDto>,
                response: Response<GetReviewInfoResponseDto>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString());

                    menu = response.body()?.menuName.toString()
                    binding.tvMenu.text = response.body()?.menuName.toString()

                    binding.tvRate.text =
                        String.format("%.1f", response.body()?.mainGrade!!.toFloat()).toFloat().toString()
                    binding.tvGradeTaste.text =
                        String.format("%.1f", response.body()?.tasteGrade!!.toFloat()).toFloat().toString()
                    binding.tvGradeAmount.text =
                        String.format("%.1f", response.body()?.amountGrade!!.toFloat()).toFloat().toString()

                    binding.tvReviewNumCount.text = response.body()?.totalReviewCount.toString()
                    binding.rbAverageRate.rating=response.body()?.mainGrade!!.toFloat()

                    val cnt = response.body()?.totalReviewCount!!

                    binding.progressBar1.max = cnt
                    binding.progressBar2.max = cnt
                    binding.progressBar3.max = cnt
                    binding.progressBar4.max = cnt
                    binding.progressBar5.max = cnt

                    binding.progressBar1.progress= response.body()?.reviewGradeCnt?.oneCnt!!
                    binding.progressBar2.progress= response.body()?.reviewGradeCnt?.twoCnt!!
                    binding.progressBar3.progress= response.body()?.reviewGradeCnt?.threeCnt!!
                    binding.progressBar4.progress= response.body()?.reviewGradeCnt?.fourCnt!!
                    binding.progressBar5.progress= response.body()?.reviewGradeCnt?.fiveCnt!!
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<GetReviewInfoResponseDto>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    private fun lodeData(id: Long) {
        val reviewService = RetrofitImpl.retrofit.create(ReviewService::class.java)
        reviewService.getReview("FIX",id).enqueue(object :
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
}