package com.eatssu.android.ui.review.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.data.RetrofitImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportViewModel : ViewModel() {

    private val _isDone = MutableLiveData<Boolean>()
    val isDone: LiveData<Boolean> get() = _isDone

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun postData(reviewId: Long, reportType: String, content: String) {
        val service = RetrofitImpl.retrofit.create(ReportService::class.java)

        viewModelScope.launch(Dispatchers.IO) {
            service.reportReview(ReportRequest(reviewId, reportType, content))
                .enqueue(object : Callback<BaseResponse<Void>> {
                    override fun onResponse(
                        call: Call<BaseResponse<Void>>,
                        response: Response<BaseResponse<Void>>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                handleSuccessResponse("신고가 완료되었습니다.")
                            } else {
                                handleErrorResponse("신고가 실패하였습니다.")
                            }
                        }
                    }

                override fun onFailure(call: Call<BaseResponse<Void>>, t: Throwable) {
                    handleErrorResponse("신고가 실패하였습니다.")
                }
            })
        }
    }

    fun handleSuccessResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isDone.value = true

        }
    }

    fun handleErrorResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isDone.value = false
        }
    }
}
