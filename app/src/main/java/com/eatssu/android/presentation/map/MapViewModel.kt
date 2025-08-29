package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.usecase.user.GetPartnershipDetailUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.map.model.RestaurantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MapState(
    val showDepartmentBottomSheet: Boolean = false,
    val showPartnershipBottomSheet: Boolean = false,
    val partnerships: List<Partnership> = emptyList(),
    val restaurantPartnershipInfo: PartnershipRestaurant? = null,
    val restaurantInfoList: List<RestaurantInfo> = emptyList(),
    var partnershipToggleText: String = "내 제휴"
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository,
    private val getPartnershipDetailUseCase: GetPartnershipDetailUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MapState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MapState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent


    init {
        loadPartnerships()
    }

    // 제휴 정보 로딩
    fun loadPartnerships() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            runCatching { partnershipRepository.getAllPartnerships() }
                .onSuccess { data ->
                    _uiState.value = UiState.Success(MapState(partnerships = data))
                }
                .onFailure {
                    Timber.e(it, "제휴 정보 로딩 실패")
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("제휴 정보를 불러오지 못했습니다."))
                }
        }
    }

    // 사용자 단과대 제휴 정보 로딩
    fun loadUserCollegePartnerships() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            runCatching { partnershipRepository.getUserCollegePartnerships() }
                .onSuccess { data ->

                    _uiState.value = UiState.Success(MapState(partnerships = data))
                }
                .onFailure {
                    Timber.e(it, "사용자 단과대 제휴 정보 로딩 실패")
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("내 단과대 제휴 정보를 불러오지 못했습니다."))
                }
        }
    }

    fun selectPartnershipByStoreName(storeName: String, partnershipId: Int? = null) {
        val current = uiState.value
        if (current !is UiState.Success) return
        val data = current.data ?: return

        val restaurant = getPartnershipDetailUseCase(data.partnerships, storeName, partnershipId)
            ?: return

        val restaurantInfo = RestaurantInfo(
            collegeName = restaurant.collegeName,
            departmentName = restaurant.departmentName,
            period = "${restaurant.startDate} ~ ${restaurant.endDate}",
            benefit = restaurant.description
        )

        _uiState.value = UiState.Success(
            data.copy(
                showPartnershipBottomSheet = true,
                restaurantPartnershipInfo = restaurant,
                restaurantInfoList = listOf(restaurantInfo)
            )
        )
    }

    // 학과 정보 입력 bottomSheet 보여주기 toggle
    fun toggleDepartmentBottomSheet() {
        val current = uiState.value
        if (current is UiState.Success) {
            current.data?.let { data ->
                _uiState.value = UiState.Success(
                    data.copy(showDepartmentBottomSheet = !data.showDepartmentBottomSheet)
                )
            }
        }
    }

    // 식당별 제휴 정보 bottomSheet 보여주기 toggle
    fun togglePartnershipBottomSheet() {
        val current = uiState.value
        if (current is UiState.Success) {
            current.data?.let { data ->
                _uiState.value = UiState.Success(
                    data.copy(showPartnershipBottomSheet = !data.showPartnershipBottomSheet)
                )
            }
        }
    }
}
