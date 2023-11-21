package com.eatssu.android.ui.review.write

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UploadReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _isUpload = MutableLiveData<Boolean>()
    val isUpload: LiveData<Boolean> get() = _isUpload

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    /*
    private val _reviewMenuId = MutableLiveData<Long>()
    val reviewMenuId: LiveData<Long> = _reviewMenuId

    private val _reviewFiles = MutableLiveData<List<MultipartBody.Part>>()
    val reviewFiles: LiveData<List<MultipartBody.Part>> = _reviewFiles

    private val _reviewData = MutableLiveData<RequestBody>()
    val reviewData: LiveData<RequestBody> = _reviewData*/




    fun postReview(
        menuId: Long,
        compressedPartsList : List<MultipartBody.Part>,
        reviewData: RequestBody
    ) {
        viewModelScope.launch {

            repository.writeReview(
                menuId, compressedPartsList, reviewData
            ).enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        handleSuccessResponse("리뷰 작성에 성공하였습니다.")
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 리뷰 작성 성공: " + response.body().toString())
//                        onClickEvent()

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        handleErrorResponse("리뷰 작성에 실패했습니다.")
                        Log.d("post", "onResponse 리뷰 작성 실패")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    handleErrorResponse("리뷰 작성에 실패했습니다.")
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
        }
    }

    fun postReview(
        menuId: Long,
        reviewData: RequestBody
    ) {
        viewModelScope.launch {

            repository.writeReview(
                menuId, reviewData
            ).enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        handleSuccessResponse("리뷰 작성에 성공하였습니다.")
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 리뷰 작성 성공: " + response.body().toString())
//                        onClickEvent()

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        handleErrorResponse("리뷰 작성에 실패했습니다.")
                        Log.d("post", "onResponse 리뷰 작성 실패")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    handleErrorResponse("리뷰 작성에 실패했습니다.")
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
        }
    }


    private fun handleSuccessResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isUpload.value = true

        }
    }



    private fun handleErrorResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isUpload.value = false
        }
    }
}