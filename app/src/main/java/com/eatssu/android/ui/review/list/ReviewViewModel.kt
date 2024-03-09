package com.eatssu.android.ui.review.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.Review
import com.eatssu.android.data.service.ReviewService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReviewViewModel(private val reviewService: ReviewService) : ViewModel() {

    private val _itemState: MutableStateFlow<ItemState> =
        MutableStateFlow(ItemState(MenuType.FIXED, 0))
    val ItemState: StateFlow<ItemState> = _itemState.asStateFlow()

    private val _state: MutableStateFlow<ReviewState> = MutableStateFlow(ReviewState())
    val state: StateFlow<ReviewState> = _state.asStateFlow()

//    private val _reviewList = MutableLiveData<GetReviewListResponse>()
//    val reviewList: LiveData<GetReviewListResponse> = _reviewList
//
//    private val _reviewInfo = MutableLiveData<GetReviewInfoResponse>()
//    val reviewInfo: LiveData<GetReviewInfoResponse> = _reviewInfo


    fun loadMenuReviewInfo(
        menuId: Long,
    ) {
        viewModelScope.launch {

            reviewService.getMenuReviewInfo(menuId)
                .enqueue(object : Callback<BaseResponse<GetMenuReviewInfoResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetMenuReviewInfoResponse>>,
                        response: Response<BaseResponse<GetMenuReviewInfoResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.result!!


                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            _state.update {
                                it.copy(
//                                    review = data.asReview()
                                )
                            }

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<GetMenuReviewInfoResponse>>,
                        t: Throwable,
                    ) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
        }
    }

    fun loadMealReviewInfo(
        menuId: Long,
    ) {
        viewModelScope.launch {
            reviewService.getMealReviewInfo(menuId)
                .enqueue(object : Callback<BaseResponse<GetMealReviewInfoResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetMealReviewInfoResponse>>,
                        response: Response<BaseResponse<GetMealReviewInfoResponse>>,
                    ) {
                        if (response.isSuccessful) {

                            val data = response.body()?.result!!
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            _state.update {
                                it.copy(
//                                    review = data.asReview()
                                )
                            }
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<GetMealReviewInfoResponse>>,
                        t: Throwable,
                    ) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
        }
    }

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
                .enqueue(object : Callback<BaseResponse<GetReviewListResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetReviewListResponse>>,
                        response: Response<BaseResponse<GetReviewListResponse>>,
                    ) {
                        if (response.isSuccessful) {

                            val data = response.body()?.result!!

                            if (data.dataList!!.isNotEmpty()) {
                                Log.d("post", "onResponse 성공: " + response.body().toString())
                                _state.update {
                                    it.copy(
                                        error = true,
                                        reviewList = data,
                                        isEmpty = false
                                    )
                                }

                            }
                            // 정상적으로 통신이 성공된 경우


                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<GetReviewListResponse>>,
                        t: Throwable,
                    ) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
        }
    }
}

data class ItemState(
    var menuType: MenuType,
    var itemid: Long,
)

data class ReviewState(
//    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
//    var tokens: TokenResponse? = null,
    var review: Review? = null,
    var reviewList: GetReviewListResponse? = null,
    var isEmpty: Boolean = true,
    var menuType: MenuType? = MenuType.FIXED,
    var itemId: Long? = 0,
)