package com.eatssu.android.presentation.cafeteria.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.asReviewInfo
import com.eatssu.android.data.dto.response.toReviewList
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMealReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetMealReviewListUseCase
import com.eatssu.android.domain.usecase.review.GetMenuReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetMenuReviewListUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val getMenuReviewInfoUseCase: GetMenuReviewInfoUseCase,
    private val getMenuReviewListUseCase: GetMenuReviewListUseCase,
    private val getMealReviewInfoUseCase: GetMealReviewInfoUseCase,
    private val getMealReviewListUseCase: GetMealReviewListUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ReviewListState>>(UiState.Init)
    val uiState: StateFlow<UiState<ReviewListState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadReview(
        menuType: String,
        itemId: Long,
    ) {
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
            getMenuReviewInfoUseCase(menuId)

                .onStart {
                    _uiState.value = UiState.Loading
                }
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 정보를 불러오는데 실패하였습니다."))

                    Timber.d(e.toString())
                }.collect { result ->
                    result.result?.apply {
                        if (mainRating == null) {
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    reviewInfo = asReviewInfo(),
                                    isEmpty = true
                                )
                            )
                        } else {
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    reviewInfo = asReviewInfo(),
                                    isEmpty = false
                                )
                            )
                            Timber.d("리뷰 있다")
                        }
                    }
                }
        }
    }

    private fun callMealReviewInfo(
        mealId: Long,
    ) {
        viewModelScope.launch {
            getMealReviewInfoUseCase(mealId)
                .onStart {
                    _uiState.value = UiState.Loading

                }.catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 정보를 불러오는데 실패했습니다."))

                    Timber.e(e.toString())
                }.collect { result ->
                    result.result?.apply {
                        if (mainRating == null) {
                            _uiState.value = UiState.Success(
                                ReviewListState(
//                                    reviewInfo = asReviewInfo(),
                                    isEmpty = true
                                )
                            )
                        } else {
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    reviewInfo = asReviewInfo(),
                                    isEmpty = false
                                )
                            )
                            Timber.d("리뷰 있다")
                        }
                    }
                }


        }
    }


    private fun callMenuReviewList(
        itemId: Long,
    ) {
        viewModelScope.launch {
            getMenuReviewListUseCase(itemId)
                .onStart {
                    _uiState.value = UiState.Loading
                }.catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 조회에 실패했습니다."))

                    Timber.e(e.toString())
                }.collect { result ->
                    result.result?.apply {
                        if (numberOfElements == 0) { //리뷰 없음
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    isEmpty = true,
                                )
                            )
                        } else { //리뷰 있음
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    reviewList = this.toReviewList(),
                                    isEmpty = false
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun callMealReviewList(
        itemId: Long,
    ) {
        viewModelScope.launch {
            getMealReviewListUseCase(itemId)
                .onStart {
                    _uiState.value = UiState.Loading
                }.catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 조회에 실패했습니다."))

                    Timber.e(e.toString())
                }.collect { result ->
                    result.result?.apply {
                        if (numberOfElements == 0) { //리뷰 없음
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    isEmpty = true,
                                )
                            )
                        } else { //리뷰 있음
                            _uiState.value = UiState.Success(
                                ReviewListState(
                                    reviewList = this.toReviewList(),
                                    isEmpty = false
                                )
                            )
                        }
                    }
                }
        }
    }

//    fun deleteReview(reviewId: Long) {
//        viewModelScope.launch {
//            deleteReviewUseCase(reviewId).onStart {
//                _uiState.update { it.copy(loading = true) }
//            }.onCompletion {
//                _uiState.update { it.copy(loading = false, error = true) }
//            }.catch { e ->
//                _uiState.update {
//                    it.copy(
//                        error = true,
////                        toastMessage = context.getString(R.string.delete_not)
//                    )
//                }
//                Timber.e(e.toString())
//            }.collectLatest { result ->
//                Timber.d(result.toString())
//
//                _uiState.update {
//                    it.copy(
////                        isDeleted = true,
////                        toastMessage = context.getString(R.string.delete_done)
//                    )
//                }
//            }
//        }
//    }
}

data class ReviewListState(
    val isEmpty: Boolean = true,
    val reviewInfo: ReviewInfo? = null,
    val reviewList: List<Review>? = null
)