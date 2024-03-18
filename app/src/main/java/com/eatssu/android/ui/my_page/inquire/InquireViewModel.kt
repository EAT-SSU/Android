package com.eatssu.android.ui.my_page.inquire

import android.util.Log
import androidx.lifecycle.ViewModel
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.InquiriesRequest
import com.eatssu.android.data.service.InquiresService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InquireViewModel(private val inquiresService: InquiresService) : ViewModel() {

    private val _uiState: MutableStateFlow<InquireState> = MutableStateFlow(InquireState())
    val uiState: StateFlow<InquireState> = _uiState.asStateFlow()

    fun inquireContent(content: String) {
        inquiresService.inquireContent(InquiriesRequest(content))
            .enqueue(object : Callback<BaseResponse<InquiriesRequest>> {
                override fun onResponse(
                    call: Call<BaseResponse<InquiriesRequest>>,
                    response: Response<BaseResponse<InquiriesRequest>>,
                ) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            Log.d(
                                "InquireActivity",
                                "onResponse 성공: 문의하기" + response.body().toString()
                            )

                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    error = false,
                                    toastMessage = "문의가 완료되었습니다."
                                )
                            }

                        } else {
                            Log.d(
                                "InquireActivity",
                                "onResponse 오류: 문의하기" + response.body().toString()
                            )
                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    error = true,
                                    toastMessage = "문의 진행 중 오류가 발생하였습니다."
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<InquiriesRequest>>, t: Throwable) {
                    Log.d("InquireActivity", "onFailure 에러: 문의하기" + t.message.toString())
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = true,
                            toastMessage = "문의 진행 중 오류가 발생하였습니다."
                        )
                    }
                }
            })
    }
}

data class InquireState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",
)