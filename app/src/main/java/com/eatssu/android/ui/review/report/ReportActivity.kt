package com.eatssu.android.ui.review.report

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.ReportType
import com.eatssu.android.databinding.ActivityReportBinding

class ReportActivity : BaseActivity<ActivityReportBinding>(ActivityReportBinding::inflate) {
    private var reviewId = -1L
    private lateinit var viewModel: ReportViewModel
    private var reportType = ""
    private var content = ""

    //Todo 코드 수정 해야함
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

            reportType = selectedReportType.toString()
            content =
                getString(selectedReportType.description) ?: binding.etReportComment.text.toString()

            Log.d("ReportActivity", reportType)
            Log.d("ReportActivity", reviewId.toString())

            viewModel.postData(reviewId, reportType, content)
        }
    }

    private fun getSelectedReportType(selectedId: Int): ReportType {
        return when (selectedId) {
            binding.radioBt1.id -> ReportType.NO_ASSOCIATE_CONTENT
            binding.radioBt2.id -> ReportType.IMPROPER_CONTENT
            binding.radioBt3.id -> ReportType.IMPROPER_ADVERTISEMENT
            binding.radioBt4.id -> ReportType.COPY
            binding.radioBt5.id -> ReportType.COPYRIGHT
            binding.radioBt6.id -> ReportType.EXTRA
            else -> ReportType.EXTRA // Default to ETC if none selected
        }
    }

    private fun observeViewModel() {
        viewModel.toastMessage.observe(this) { result ->
            Toast.makeText(this@ReportActivity, result, Toast.LENGTH_SHORT).show()
        }

        viewModel.isDone.observe(this) { isDone ->
            if(isDone) {
                finish()
            }
        }
    }
}