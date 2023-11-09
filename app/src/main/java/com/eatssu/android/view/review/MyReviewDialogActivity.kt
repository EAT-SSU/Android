package com.eatssu.android.view.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyReviewDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewDialogBinding
    var reviewId = -1L
    var menu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyReviewDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()

        Log.d("reviewId", reviewId.toString())

        binding.btnReviewFix.setOnClickListener {
            val intent = Intent(this, FixedReviewActivity::class.java)
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("menu", menu)
            startActivity(intent)
            finish()
        }

        binding.btnReviewDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("리뷰 삭제")
                setMessage("작성한 리뷰를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(
                        this@MyReviewDialogActivity,
                        "리뷰 삭제가 취소되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setPositiveButton("삭제") { _, _ ->
                    val service = RetrofitImpl.retrofit.create(ReviewService::class.java)

                    service.delReview(reviewId)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    if (response.code() == 200) {
                                        Log.d("post", "onResponse 성공: " + response.body().toString())
                                        Toast.makeText(
                                            this@MyReviewDialogActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT
                                        ).show()
                                        finish()

                                    } else {
                                        Log.d("post", "onResponse 오류: " + response.body().toString())
                                        Toast.makeText(
                                            this@MyReviewDialogActivity, "삭제가 실패하였습니다.", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d("post", "onFailure 에러: " + t.message.toString())
                                Toast.makeText(
                                    this@MyReviewDialogActivity, "삭제가 실패하였습니다.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                show()
                }
            }
        }
    }