package com.eatssu.android.presentation.review.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.dto.response.asReviewInfo
import com.eatssu.android.data.dto.response.toReviewList
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.service.ReviewService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//@HiltViewModel
class ReviewViewModel(
//@Inject constructor(
    private val reviewService: ReviewService,
//    private val reviewRepository: ReviewRepository,
) : ViewModel() {

//    var menuType: MenuType,
//    var itemId: Long? = 0,

//    private val _itemState: MutableStateFlow<ItemState> =
//        MutableStateFlow(ItemState(MenuType.FIXED, 0))
//    val ItemState: StateFlow<ItemState> = _itemState.asStateFlow()

    private val _uiState: MutableStateFlow<ReviewState> = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()


    fun loadMenuReviewInfo(
        menuId: Long,
    ) {
        viewModelScope.launch {
//            reviewRepository.getMenuReviewInfo(menuId)
//                .catch {
//                    _uiState.value.error = true
//                }
//                .collect {
//                    _uiState.value.reviewInfo = it.result?.asReviewInfo()
//                    Log.d("it.result?.asReviewInfo()",it.result.toString())
//                }


            reviewService.getMenuReviewInfo(menuId)
                .enqueue(object : Callback<BaseResponse<GetMenuReviewInfoResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetMenuReviewInfoResponse>>,
                        response: Response<BaseResponse<GetMenuReviewInfoResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.result!!

                            // 정상적으로 통신이 성공된 경우
                            Log.d("ReviewViewModel", "onResponse 성공: " + response.body().toString())

                            if (data.mainRating == null) {
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        reviewInfo = data.asReviewInfo(),
                                        isEmpty = true
                                    )
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        reviewInfo = data.asReviewInfo(),
                                        isEmpty = false
                                    )
                                }
                                Log.d("ReviewViewModel", "리뷰 있다고")
                            }

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("ReviewViewModel", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<GetMenuReviewInfoResponse>>,
                        t: Throwable,
                    ) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("ReviewViewModel", "onFailure 에러: " + t.message.toString())
                    }
                })

        }
    }

    fun loadMealReviewInfo(
        mealId: Long,
    ) {
        viewModelScope.launch {
            reviewService.getMealReviewInfo(mealId)
                .enqueue(object : Callback<BaseResponse<GetMealReviewInfoResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetMealReviewInfoResponse>>,
                        response: Response<BaseResponse<GetMealReviewInfoResponse>>,
                    ) {
                        if (response.isSuccessful) {

                            val data = response.body()?.result!!
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    error = false,
                                    reviewInfo = data.asReviewInfo()
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
                            Log.d("post", "onResponse 성공: " + response.body().toString())

                            val data = response.body()?.result!!

                            if (data.numberOfElements == 0) { //리뷰 없어 시방
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        isEmpty = true
                                    )
                                }
                            } else { //리뷰 있음
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        reviewList = data.toReviewList(),
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

//data class ItemState(
//    var menuType: MenuType,
//    var itemid: Long,
//)

data class ReviewState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var isEmpty: Boolean = true, //리뷰 없다~

    var reviewInfo: ReviewInfo? = null,
    var reviewList: List<Review>? = null,
)