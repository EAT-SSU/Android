package com.eatssu.android.ui.review.etc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.util.RetrofitImpl.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportViewModel : ViewModel() {

    // 서버 통신 결과를 LiveData로 감싸서 UI에 업데이트할 수 있도록 함
    val reportResult = MutableLiveData<String>()

    fun postData(reviewId: Long, reportType: String, content: String) {
        val service = retrofit.create(ReportService::class.java)

        service.reportReview(ReportRequestDto(reviewId, reportType, content))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            reportResult.value = "신고가 완료되었습니다."
                        } else {
                            reportResult.value = "신고가 실패하였습니다."
                        }
                    } else {
                        reportResult.value = "서버 오류: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    reportResult.value = "네트워크 오류: ${t.message}"
                }
            })
    }
}
