package com.eatssu.android.ui.review

import RetrofitImpl
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.eatssu.android.R
import com.eatssu.android.data.model.request.ReportRequest
import com.eatssu.android.data.model.response.TokenResponse
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.ui.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

// reviewId 받아오는거 해야함
// 메인 리뷰에서 신고하기 뷰로 넘어가는거 해야함

abstract class ReportActivity : BaseActivity() {
    private lateinit var binding: ActivityReportBinding
    var reviewId = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)

        setActionBarTitle("신고하기")

        setContentView(binding.root)

        ReportInfo();

    }

    abstract fun setActionBarTitle(s: String)

    private fun ReportInfo() {
        val radioGroup = binding.radioGp;
        var radioNum = -1;
        var reportType = ""
        var content = ""
        radioGroup.setOnClickListener {
            if (binding.radioBt1.isChecked) {
                radioNum = 1;
                reportType = "CONTENT"
                content = getString(R.string.report1);
            }
            else if (binding.radioBt2.isChecked) {
                radioNum = 2;
                reportType = "BAD_WORD"
                content = getString(R.string.report2);
            }
            else if (binding.radioBt3.isChecked) {
                radioNum = 3;
                reportType = "AD"
                content = getString(R.string.report3);
            }
            else if (binding.radioBt4.isChecked) {
                radioNum = 4;
                reportType = "COPY"
                content = getString(R.string.report4);
            }
            else if (binding.radioBt5.isChecked) {
                radioNum = 5;
                reportType = "COPYRIGHT"
                content = getString(R.string.report5);
            }
            else if (binding.radioBt6.isChecked) {
                radioNum = 6;
                reportType = "ETC"
                content = binding.etReportComment.toString()
            }
        }

        postData(reportType, content);
    }

    fun postData(reportType: String , content: String) {
        val service = RetrofitImpl.mRetrofit.create(ReportService::class.java)
        service.reportReview(reviewId, reportType, content)
            .enqueue(object : Callback<ReportRequest> {
                override fun onResponse(
                    call: Call<ReportRequest>,
                    response: Response<ReportRequest>
                ) {
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
                }

                override fun onFailure(call: Call<ReportRequest>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }
}