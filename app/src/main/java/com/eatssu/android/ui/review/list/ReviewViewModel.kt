package com.eatssu.android.ui.review.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.dto.response.GetReviewInfoResponseDto
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.service.ReviewService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReviewViewModel(private val reviewService: ReviewService) : ViewModel() {

    private val _reviewList = MutableLiveData<GetReviewListResponse>()
    val reviewList: LiveData<GetReviewListResponse> = _reviewList

    private val _reviewInfo = MutableLiveData<GetReviewInfoResponseDto>()
    val reviewInfo: LiveData<GetReviewInfoResponseDto> = _reviewInfo

    fun loadReviewList(
        menuType: MenuType,
        mealId: Long?,
        menuId: Long?,
//        lastReviewId: Long?,
//        page: Int?,
//        size: Int?,
    ) {
        viewModelScope.launch {

            reviewService.getReviewList(menuType.toString(), mealId, menuId)
                .enqueue(object : Callback<GetReviewListResponse> {
                    override fun onResponse(
                        call: Call<GetReviewListResponse>, response: Response<GetReviewListResponse>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            _reviewList.postValue(response.body())

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<GetReviewListResponse>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
        }
    }

    fun loadReviewInfo(
        menuType: MenuType,
        mealId: Long?,
        menuId: Long?,
    ) {
        viewModelScope.launch {

            reviewService.getRreviewInfo(menuType.toString(), mealId, menuId)
                .enqueue(object : Callback<GetReviewInfoResponseDto> {
                    override fun onResponse(
                        call: Call<GetReviewInfoResponseDto>,
                        response: Response<GetReviewInfoResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            _reviewInfo.postValue(response.body())

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<GetReviewInfoResponseDto>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
        }
    }
}