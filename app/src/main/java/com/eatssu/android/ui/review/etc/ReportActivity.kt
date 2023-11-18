package com.eatssu.android.ui.review.etc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.ReportType
import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.util.RetrofitImpl.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// reviewId 받아오는거 해야함
// 메인 리뷰에서 신고하기 뷰로 넘어가는거 해야함

class ReportActivity : BaseActivity<ActivityReportBinding>(ActivityReportBinding::inflate) {
    private var reviewId = -1L
    private var reportType = ""
    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "신고하기" // 툴바 제목 설정

        reportInfo()
    }

    private fun reportInfo() {

        binding.btnSendReport.setOnClickListener {

            val selectedReportType = getSelectedReportType(binding.radioGp.checkedRadioButtonId)
            reviewId = intent.getLongExtra("reviewId", -1L)
            reportType = selectedReportType.type
            content = selectedReportType.defaultContent?.let { getString(it) } ?: binding.etReportComment.text.toString()
            Log.d("ReportActivity", reportType)

            Log.d("ReportActivity", reviewId.toString())

            postData(reviewId, reportType, content)

            finish()
        }

    }

    private fun getSelectedReportType(selectedId: Int): ReportType {
        return when (selectedId) {
            binding.radioBt1.id -> ReportType.CONTENT
            binding.radioBt2.id -> ReportType.BAD_WORD
            binding.radioBt3.id -> ReportType.AD
            binding.radioBt4.id -> ReportType.COPY
            binding.radioBt5.id -> ReportType.COPYRIGHT
            binding.radioBt6.id -> ReportType.ETC
            else -> ReportType.ETC // Default to ETC if none selected
        }
    }

    private fun postData(reviewId: Long, reportType: String, content: String) {
        val service = retrofit.create(ReportService::class.java)
        Log.d("reportId", reviewId.toString())

        service.reportReview(ReportRequestDto(reviewId, reportType, content))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            Log.d("ReportActivity", "onResponse 성공: " + response.body().toString())
                            Toast.makeText(
                                this@ReportActivity, "신고가 완료되었습니다.", Toast.LENGTH_SHORT
                            ).show()
                            finish()

                        } else {
                            Log.d("ReportActivity", "onResponse 오류: " + response.body().toString())
                            Toast.makeText(
                                this@ReportActivity, "신고가 실패하였습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("ReportActivity", "onFailure 에러: " + t.message.toString())
                    Toast.makeText(
                        this@ReportActivity, "신고가 실패하였습니다.", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}