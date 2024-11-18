package com.eatssu.android.ui.mypage.myreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.toReviewList
import com.eatssu.android.data.model.Review
import com.eatssu.android.data.usecase.auth.GetMyReviewsUseCase
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
class MyReviewViewModel @Inject constructor(
    private val getMyReviewsUseCase: GetMyReviewsUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<MyReviewState> = MutableStateFlow(MyReviewState())
    val uiState: StateFlow<MyReviewState> = _uiState.asStateFlow()

    init {
        getMyReviews()
    }

    fun getMyReviews() {
        viewModelScope.launch {
            getMyReviewsUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())

                result.result?.apply {
                    if (dataList.isEmpty()) {
                        _uiState.update { it.copy(isEmpty = true) }
                    } else {
                        //Todo 리뷰 바인딩을...
                        _uiState.update { it.copy(myReviews = this.toReviewList()) }
                    }


                }
            }
        }
    }
}


data class MyReviewState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var isEmpty: Boolean = false,

    var myReviews: List<Review>? = null,

    )