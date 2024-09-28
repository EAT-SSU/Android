package com.eatssu.android.ui.review.report

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.ReportType
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ReportActivity : BaseActivity<ActivityReportBinding>(ActivityReportBinding::inflate) {
    private val reportViewModel: ReportViewModel by viewModels()

    private var reviewId = -1L
    private var reportType = ""
    private var content = ""

    private var inputText: String = ""


    //Todo 코드 수정 해야함
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "신고하기" // 툴바 제목 설정

        reviewId = intent.getLongExtra("reviewId", -1L)

        binding.etReportComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                inputText = binding.etReportComment.text.trim().toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        reportInfo()
    }

    private fun reportInfo() {
        binding.btnSendReport.setOnClickListener {

            val selectedReportType = getSelectedReportType(binding.radioGp.checkedRadioButtonId)

            reportType = selectedReportType.toString()

            content = if (selectedReportType == ReportType.EXTRA) {
                inputText
            } else {
                getString(selectedReportType.description)
            }

            reportViewModel.postData(reviewId, reportType, content)

            lifecycleScope.launch {
                reportViewModel.uiState.collectLatest {
                    showToast(it.toastMessage)
                    if (it.isDone) {
                        finish()
                    }
                }
            }
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
}