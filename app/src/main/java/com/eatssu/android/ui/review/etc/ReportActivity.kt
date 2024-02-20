package com.eatssu.android.ui.review.etc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.ReportType
import com.eatssu.android.databinding.ActivityReportBinding

class ReportActivity : BaseActivity<ActivityReportBinding>(ActivityReportBinding::inflate) {
    private var reviewId = -1L
    private lateinit var viewModel: ReportViewModel
    private var reportType = ""
    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "신고하기" // 툴바 제목 설정

        reviewId = intent.getLongExtra("reviewId", -1L)

        viewModel = ViewModelProvider(this).get(ReportViewModel::class.java)

        reportInfo()

        observeViewModel()
    }

    private fun reportInfo() {
        binding.btnSendReport.setOnClickListener {

            val selectedReportType = getSelectedReportType(binding.radioGp.checkedRadioButtonId)

            reportType = selectedReportType.type
            content = selectedReportType.defaultContent?.let { getString(it) } ?: binding.etReportComment.text.toString()

            Log.d("ReportActivity", reportType)
            Log.d("ReportActivity", reviewId.toString())

            viewModel.postData(reviewId, reportType, content)
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

    private fun observeViewModel() {
        viewModel.toastMessage.observe(this, Observer { result ->
            Toast.makeText(this@ReportActivity, result, Toast.LENGTH_SHORT).show()
        })

        viewModel.isDone.observe(this) { isDone ->
            if(isDone) {
                finish()
            }
        }
    }
}