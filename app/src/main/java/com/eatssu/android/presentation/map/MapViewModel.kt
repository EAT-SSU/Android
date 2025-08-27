package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.presentation.map.model.RestaurantInfo
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
    val restaurantInfoList: List<RestaurantInfo> = emptyList(),
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

    fun selectPartnershipByStoreName(storeName: String, partnershipId: Int? = null) {
        val matched = _uiState.value.partnerships.find { it.storeName == storeName }
        matched?.let { partnership ->

            // 특정 id로 찾거나, 없으면 첫 번째로 fallback
            val targetInfo = partnershipId?.let { id ->
                partnership.partnershipInfos.find { it.id == id }
            } ?: partnership.partnershipInfos.firstOrNull()

            targetInfo?.let { info ->
                val restaurant = PartnershipRestaurant(
                    id = info.id,
                    partnershipType = info.partnershipType,
                    storeName = partnership.storeName,
                    description = info.description,
                    startDate = info.startDate,
                    endDate = info.endDate,
                    restaurantType = partnership.restaurantType ,
                    longitude = partnership.longitude,
                    latitude = partnership.latitude,
                    collegeName = info.collegeName,
                    departmentName = info.departmentName,
                    partnershipLikeCount = info.likeCount,
                    likedByUser = info.isLiked
                )

                val restaurantInfos = partnership.partnershipInfos.map {
                    RestaurantInfo(
                        collegeName = it.collegeName,
                        departmentName = it.departmentName,
                        period = "${it.startDate} ~ ${it.endDate}",
                        benefit = it.description
                    )
                }

                _uiState.update { state ->
                    state.copy(
                        showPartnershipBottomSheet = true,
                        restaurantPartnershipInfo = restaurant,
                        restaurantInfoList = restaurantInfos
                    )
                }
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
