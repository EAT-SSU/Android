package com.eatssu.android.view.review

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.RetrofitImpl.mRetrofit
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    val service = mRetrofit.create(ReviewService::class.java)
                    lifecycleScope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                service.delReview(reviewId).execute()
                            }
                            Log.d("post", "응답 코드: ${response.code()}")
                            Log.d("post", "응답 본문: ${response.body()}")

                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공한 경우
                                Log.d("post", "onResponse 성공: " + response.body().toString())
                                Toast.makeText(
                                    this@MyReviewDialogActivity,
                                    "삭제하기가 완료되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                Log.d(
                                    "post",
                                    "onResponse 실패 write" + response.code()
                                )
                                finish()
                            }
                        } catch (e: Exception) {
                            // 통신 중 예외가 발생한 경우
                            Log.d("post", "통신 실패: ${e.message}")
                            Toast.makeText(
                                this@MyReviewDialogActivity, "리뷰를 삭제하지 못했습니다.", Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
                create()
                show()
            }
        }
    }
}