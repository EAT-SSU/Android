package com.eatssu.android.presentation.goodprice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.usecase.goodprice.GetGoodPriceStoreDetailUseCase
import com.eatssu.android.domain.usecase.goodprice.GetGoodPriceStoresUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import com.eatssu.common.enums.GoodPriceCategory
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 착한가격업소 지도 ViewModel
 */
@HiltViewModel
class GoodPriceMapViewModel @Inject constructor(
    private val getGoodPriceStoresUseCase: GetGoodPriceStoresUseCase,
    private val getGoodPriceStoreDetailUseCase: GetGoodPriceStoreDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoodPriceMapState())
    val uiState: StateFlow<GoodPriceMapState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        // 초기 진입 시 전체 업소 목록 조회
        loadStores(GoodPriceCategory.ALL)
    }

    // 카테고리 필터 변경 (중복 선택 불가, 단일 선택)
    fun setCategory(category: GoodPriceCategory) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        loadStores(category)
    }

    // 업소 목록 API 호출
    private fun loadStores(category: GoodPriceCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getGoodPriceStoresUseCase(category)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            stores = result.data,
                            isLoading = false,
                        )
                    }
                }

                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                    // 업소 정보를 못 불러온 경우 토스트 알림
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            UiText.StringResource(R.string.toast_load_good_price_stores_failed),
                            ToastType.ERROR,
                        )
                    )
                }
            }
        }
    }

    // 특정 업소 마커 클릭 시 상세 정보 조회
    fun selectStore(storeId: Long) {
        viewModelScope.launch {
            when (val result = getGoodPriceStoreDetailUseCase(storeId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(selectedStoreDetail = result.data) }
                }

                else -> {
                    // 상세 정보 조회 실패 시에도 동일한 에러 토스트 표시
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            UiText.StringResource(R.string.toast_load_good_price_stores_failed),
                            ToastType.ERROR,
                        )
                    )
                }
            }
        }
    }

    // 바텀시트 닫을 때 선택된 업소 정보 초기화
    fun clearSelectedStore() {
        _uiState.update { it.copy(selectedStoreDetail = null) }
    }
}
