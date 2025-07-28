package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.presentation.map.model.MapRestaurantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    val mapRestaurantInfos: List<MapRestaurantInfo> = emptyList(),
    var partnershipToggleText: String = "내 제휴"
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        loadPartnerships()
    }

    // 제휴 정보 로딩
    fun loadPartnerships() {
        viewModelScope.launch {
            runCatching {
                partnershipRepository.getAllPartnerships()
            }.onSuccess { data ->
                _uiState.update { it.copy(partnerships = data) }
            }.onFailure {
                Timber.e("제휴 정보 로딩 실패: ${it.message}")
            }
        }
    }

    // 사용자 단과대 제휴 정보 로딩
    fun loadUserCollegePartnerships() {
        viewModelScope.launch {
            runCatching {
                partnershipRepository.getUserCollegePartnerships()
            }.onSuccess { data ->
                _uiState.update { it.copy(partnerships = data) }
            }.onFailure {
                Timber.e("사용자 단과대 제휴 정보 로딩 실패: ${it.message}")
            }
        }
    }

    fun selectPartnershipByStoreName(storeName: String) {
        val matched = _uiState.value.partnerships.find { it.storeName == storeName }
        matched?.let {
            val restaurant = PartnershipRestaurant(
                id = it.partnershipInfos.firstOrNull()?.id ?: 0,
                partnershipType = it.partnershipInfos.firstOrNull()?.partnershipType ?: "",
                storeName = it.storeName,
                description = it.partnershipInfos.firstOrNull()?.description ?: "",
                startDate = it.partnershipInfos.firstOrNull()?.startDate ?: "",
                endDate = it.partnershipInfos.firstOrNull()?.endDate ?: "",
                restaurantType = it.restaurantType,
                longitude = it.longitude,
                latitude = it.latitude,
                collegeName = it.partnershipInfos.firstOrNull()?.collegeName ?: "",
                departmentName = it.partnershipInfos.firstOrNull()?.departmentName ?: "",
                partnershipLikeCount = it.partnershipInfos.firstOrNull()?.likeCount ?: 0,
                likedByUser = it.partnershipInfos.firstOrNull()?.isLiked ?: false
            )

            val restaurantInfos = it.partnershipInfos.map { info ->
                MapRestaurantInfo(
                    collegeName = info.collegeName,
                    departmentName = info.departmentName,
                    period = "${info.startDate} ~ ${info.endDate}",
                    benefit = info.description
                )
            }

            _uiState.update { state ->
                state.copy(
                    showPartnershipBottomSheet = true,
                    restaurantPartnershipInfo = restaurant,
                    mapRestaurantInfos = restaurantInfos
                )
            }
        }
    }

    // 학과 정보 입력 bottomSheet 보여주기 toggle
    fun toggleDepartmentBottomSheet() {
        _uiState.update { it.copy(showDepartmentBottomSheet = !it.showDepartmentBottomSheet) }
    }

    // 식당별 제휴 정보 bottomSheet 보여주기 toggle
    fun togglePartnershipBottomSheet() {
        _uiState.update { it.copy(showPartnershipBottomSheet = !it.showPartnershipBottomSheet) }
    }
}
