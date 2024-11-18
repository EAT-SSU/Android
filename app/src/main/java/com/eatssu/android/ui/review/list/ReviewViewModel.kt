package com.eatssu.android.ui.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.asReviewInfo
import com.eatssu.android.data.dto.response.toReviewList
import com.eatssu.android.data.model.Review
import com.eatssu.android.data.model.ReviewInfo
import com.eatssu.android.data.usecase.review.GetMealReviewInfoUseCase
import com.eatssu.android.data.usecase.review.GetMealReviewListUseCase
import com.eatssu.android.data.usecase.review.GetMenuReviewInfoUseCase
import com.eatssu.android.data.usecase.review.GetMenuReviewListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
) : ViewModel() {

    private val _uiState: MutableStateFlow<ReviewState> = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()

    fun loadReview(
        menuType: String,
        itemId: Long,
    ) {
        when (menuType) {
            "FIXED" -> {
                callMenuReviewInfo(itemId)
                callMenuReviewList(itemId)
            }

            "VARIABLE" -> {
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
            getMenuReviewInfoUseCase(menuId).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                    )
                }
                Timber.d(e.toString())
            }.collectLatest { result ->
                result.result?.apply {
                    if (mainRating == null) {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                reviewInfo = asReviewInfo(),
                                isEmpty = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                reviewInfo = asReviewInfo(),
                                isEmpty = false
                            )
                        }
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
            getMealReviewInfoUseCase(mealId).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                result.result?.apply {
                    if (mainRating == null) {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                reviewInfo = asReviewInfo(),
                                isEmpty = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                reviewInfo = asReviewInfo(),
                                isEmpty = false
                            )
                        }
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
            getMenuReviewListUseCase(itemId).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                result.result?.apply {
                    if (numberOfElements == 0) { //리뷰 없음
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
                                reviewList = this.toReviewList(),
                                isEmpty = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun callMealReviewList(
        itemId: Long,
    ) {
        viewModelScope.launch {
            getMealReviewListUseCase(itemId).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                result.result?.apply {
                    if (numberOfElements == 0) { //리뷰 없음
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
                                reviewList = this.toReviewList(),
                                isEmpty = false
                            )
                        }
                    }
                }
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