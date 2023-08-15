package com.eatssu.android.viewmodel

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.repository.ReviewRepository
import com.eatssu.android.view.review.ReviewListActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UploadReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _reviewMenuId = MutableLiveData<Long>()
    val reviewMenuId: LiveData<Long> = _reviewMenuId

    private val _reviewFiles = MutableLiveData<List<MultipartBody.Part>>()
    val reviewFiles: LiveData<List<MultipartBody.Part>> = _reviewFiles

    private val _reviewData = MutableLiveData<RequestBody>()
    val reviewData: LiveData<RequestBody> = _reviewData


    val shouldStartActivity = MutableLiveData<Boolean>(false)

    fun onClickEvent() {
        shouldStartActivity.postValue(true)
    }

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
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 리뷰 작성 성공: " + response.body().toString());
                            onClickEvent()

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString());
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
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString());
                }
            })
        }
    }
}