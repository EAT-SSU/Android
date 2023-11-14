package com.eatssu.android.ui.review.etc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.util.RetrofitImpl
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityFixMenuBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FixedReviewActivity : BaseActivity<ActivityFixMenuBinding>(ActivityFixMenuBinding::inflate) {

    private var reviewId = -1L
    private var menuId = -1L
    private lateinit var menu: String
    private var comment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 수정하기" // 툴바 제목 설정

        menu = intent.getStringExtra("menu").toString()
        binding.menu.text = menu
        reportInfo()

    }

    private fun reportInfo() {

        reviewId = intent.getLongExtra("reviewId", -1L)
        Log.d("ReviewFixedActivity", reviewId.toString())

        /*val menuViewModel = ViewModelProvider(this@FixMenuActivity).get(MenuIdViewModel::class.java)
        menuViewModel.getData().observe(this, Observer { dataReceived ->
            menuId = dataReceived.toLong()
            Log.d("menufix", menuId.toString())
        })*/

        binding.btnFixReview.setOnClickListener {

            postData(reviewId)
            finish()
        }

    }

    private fun postData(reviewId: Long) {
        val service = RetrofitImpl.retrofit.create(ReviewService::class.java)

        comment = binding.etReview2Comment.text.toString()

        val reviewData = """
    {
        "mainGrade": ${binding.rbMain.rating.toInt()},
        "amountGrade": ${binding.rbAmount.rating.toInt()},
        "tasteGrade": ${binding.rbTaste.rating.toInt()},
        "content": "$comment"
    }
""".trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

        service.modifyReview(reviewId, reviewData)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            Toast.makeText(
                                this@FixedReviewActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT
                            ).show()
                            finish()

                        } else {
                            Log.d("post", "onResponse 오류: " + response.body().toString())
                            Toast.makeText(
                                this@FixedReviewActivity, "수정이 실패하였습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                    Toast.makeText(
                        this@FixedReviewActivity, "수정이 실패하였습니다.", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
}