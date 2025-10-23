package com.eatssu.android.presentation.cafeteria.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMealReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetMealReviewListUseCase
import com.eatssu.android.domain.usecase.review.GetMenuReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetMenuReviewListUseCase
import com.eatssu.common.enums.MenuType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val getMenuReviewInfoUseCase: GetMenuReviewInfoUseCase,
    private val getMenuReviewListUseCase: GetMenuReviewListUseCase,
    private val getMealReviewInfoUseCase: GetMealReviewInfoUseCase,
    private val getMealReviewListUseCase: GetMealReviewListUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ReviewState> = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()

    fun loadReview(menuType: String, itemId: Long) {
        when (menuType) {
            MenuType.FIXED.name -> {
                callMenuReviewInfo(itemId)
                callMenuReviewList(itemId)
            }

            MenuType.VARIABLE.name -> {
                callMealReviewInfo(itemId)
                callMealReviewList(itemId)
            }

            else -> {
                Timber.d("잘못된 식당 정보입니다.")

            }
        }

    }

    private fun callMenuReviewInfo(menuId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val menuReviewInfo = getMenuReviewInfoUseCase(menuId)
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    reviewInfo = menuReviewInfo,
                    isEmpty = menuReviewInfo == null,
                )
            }
        }
    }

    private fun callMealReviewInfo(mealId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val mealReviewInfo = getMealReviewInfoUseCase(mealId)
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    reviewInfo = mealReviewInfo,
                    isEmpty = mealReviewInfo == null,
                )
            }
        }
    }

    private fun callMenuReviewList(itemId: Long) {
        viewModelScope.launch {
            val menuReviewList = getMenuReviewListUseCase(itemId)
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    reviewList = menuReviewList,
                    isEmpty = menuReviewList.isEmpty()
                )
            }
        }
    }

    private fun callMealReviewList(itemId: Long) {
        viewModelScope.launch {
            val reviewList = getMealReviewListUseCase(itemId)
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    reviewList = reviewList,
                    isEmpty = reviewList.isEmpty()
                )
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val success = deleteReviewUseCase(reviewId)
            if (!success) {
                _uiState.update {
                    it.copy(
                        error = true,
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                )
            }
        }
    }
}

data class ReviewState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var isEmpty: Boolean = true, //리뷰 없다~

    var reviewInfo: ReviewInfo? = null,
    var reviewList: List<Review>? = null,
)