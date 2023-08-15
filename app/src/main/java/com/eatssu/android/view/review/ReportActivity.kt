package com.eatssu.android.ui.review

import RetrofitImpl
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.data.model.request.ReportRequest
import com.eatssu.android.data.model.response.TokenResponse
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.ui.BaseActivity
import com.eatssu.android.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// reviewId 받아오는거 해야함
// 메인 리뷰에서 신고하기 뷰로 넘어가는거 해야함

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private var reviewId = -1L
    private var reportType = ""
    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)

        supportActionBar?.title = "신고하기"

        setContentView(binding.root)

        reportInfo();

    }

    private fun reportInfo() {

        binding.btnSendReport.setOnClickListener {

            val selectId = binding.radioGp.checkedRadioButtonId

            if (selectId == binding.radioBt1.id) {
                reportType = "CONTENT"
                content = getString(R.string.report1);
            }
            else if (selectId == binding.radioBt2.id) {
                reportType = "BAD_WORD"
                content = getString(R.string.report2);
            }
            else if (selectId == binding.radioBt3.id) {
                reportType = "AD"
                content = getString(R.string.report3);
            }
            else if (selectId == binding.radioBt4.id) {
                reportType = "COPY"
                content = getString(R.string.report4);
            }
            else if (selectId == binding.radioBt5.id) {
                reportType = "COPYRIGHT"
                content = getString(R.string.report5);
            }
            else if (selectId == binding.radioBt6.id) {
                reportType = "ETC"
                content = binding.etReportComment.toString()
            }
            Log.d("reporting", reportType)

            reviewId = intent.getLongExtra("reviewId", -1L)

            postData(reviewId, reportType, content);
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun postData(reviewId: Long, reportType: String, content: String) {
        val service = RetrofitImpl.retrofit.create(ReportService::class.java)
        Log.d("reportId", reviewId.toString())
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.reportReview(ReportRequest(reviewId, reportType, content)).execute()
                }
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공한 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(
                        this@ReportActivity, "신고하기가 완료되었습니다.", Toast.LENGTH_SHORT
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
                    this@ReportActivity, "신고를 업로드하지 못했습니다.", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}