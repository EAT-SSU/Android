package com.eatssu.android.view.review

import RetrofitImpl
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityOthersReviewDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OthersReviewDialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOthersReviewDialogBinding
    var reviewId = -1L
    var menuId = -1L
    var menu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOthersReviewDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()


        binding.btnReviewReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra(
                "reviewId", reviewId
            )
            Log.d("dialogid", reviewId.toString())
            startActivity(intent)
            finish()
        }

        binding.btnReviewFix.setOnClickListener {
            val intent = Intent(this, FixMenuActivity::class.java)
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("menu", menu)
            startActivity(intent)
            finish()
        }

        binding.btnReviewDelete.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("리뷰 삭제")
                setMessage("작성한 리뷰를 삭제하시겠습니까?")
                setNegativeButton("취소", DialogInterface.OnClickListener{ dialog, which ->
                    Toast.makeText(this@OthersReviewDialogActivity, "리뷰 삭제가 취소되었습니다", Toast.LENGTH_SHORT).show()
                })
                setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                    val service = RetrofitImpl.retrofit.create(ReviewService::class.java)
                    lifecycleScope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                service.delReview(reviewId).execute()
                            }
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공한 경우
                                Log.d("post", "onResponse 성공: " + response.body().toString())
                                Toast.makeText(
                                    this@OthersReviewDialogActivity, "삭제하기가 완료되었습니다.", Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                Log.d(
                                    "post",
                                    "onResponse 실패 write" + response.code()
                                )
                            }
                        } catch (e: Exception) {
                            // 통신 중 예외가 발생한 경우
                            Log.d("post", "통신 실패: ${e.message}")
                            Toast.makeText(
                                this@OthersReviewDialogActivity, "리뷰를 삭제하지 못했습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                create()
                show()
            }
        }
    }
}