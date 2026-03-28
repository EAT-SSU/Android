package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.usecase.user.GetPartnershipDetailUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.map.model.RestaurantInfo
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.MapAnalyticsEvent
import com.eatssu.common.enums.StoreType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


val StoreType.iconRes: Int
    get() = when (this) {
        StoreType.CAFE -> R.drawable.ic_map_cafe
        StoreType.RESTAURANT -> R.drawable.ic_map_restaurant
        StoreType.PUB -> R.drawable.ic_map_pub
    }

data class MapState(
    val partnerships: List<Partnership> = emptyList(),
    val restaurantPartnershipInfo: PartnershipRestaurant? = null,
    val restaurantInfoList: List<RestaurantInfo> = emptyList(),
    val storeType: StoreType? = null,
    val selectedFilter: FilterType = FilterType.Mine,
    val filterChangeResult: FilterChangeResult? = null,
) {
    sealed class FilterChangeResult {
        object Success : FilterChangeResult()
        object RequiresDepartment : FilterChangeResult()
    }
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository,
    private val getPartnershipDetailUseCase: GetPartnershipDetailUseCase,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MapState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MapState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private val _departmentId = MutableStateFlow<Long>(-1)
    val departmentId: StateFlow<Long> = _departmentId.asStateFlow()

    private val _collegeId = MutableStateFlow<Long>(-1)
    val collegeId: StateFlow<Long> = _collegeId.asStateFlow()

    init {
        fetchUserCollegeDepartment()
    }

    private fun fetchUserCollegeDepartment() {
        viewModelScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            val newDepartmentId = userCollegeDepartment.userDepartment.departmentId.toLong()
            val newCollegeId = userCollegeDepartment.userCollege.collegeId.toLong()

            _departmentId.value = newDepartmentId
            _collegeId.value = newCollegeId

            // departmentId가 변경되면 필터 자동 설정
            val current = uiState.value
            val currentData = if (current is UiState.Success) current.data else MapState()
            val initialFilter = if (newDepartmentId == -1L) FilterType.All else FilterType.Mine

            _uiState.value = UiState.Success(
                MapState(selectedFilter = initialFilter)
            )

            // 초기 필터에 따라 데이터 로드
            when (initialFilter) {
                FilterType.All -> loadPartnerships()
                FilterType.Mine -> loadUserCollegePartnerships()
            }
            
            Timber.d("학과 정보 : ${userCollegeDepartment.userDepartment.departmentName}")
        }
    }

    // 필터 변경 (검증 로직 포함)
    fun setFilter(filter: FilterType) {
        val current = uiState.value
        val currentData = if (current is UiState.Success) current.data else MapState()

        // 학과 정보가 없는데 Mine 필터를 선택하려는 경우
        if (filter == FilterType.Mine && _departmentId.value == -1L) {
            // 학과 입력이 필요한 경우 결과를 MapState에 반영
            if (current is UiState.Success) {
                _uiState.value = UiState.Success(
                    currentData.copy(filterChangeResult = MapState.FilterChangeResult.RequiresDepartment)
                )
            }
            return
        }

        // 필터 변경 성공
        val updatedData = currentData.copy(
            restaurantPartnershipInfo = null,
            selectedFilter = filter,
            filterChangeResult = null,
        )
        _uiState.value = UiState.Success(updatedData)

        // 필터에 따라 데이터 로드
        when (filter) {
            FilterType.All -> {
                loadPartnerships()
            }

            FilterType.Mine -> {
                loadUserCollegePartnerships()
                analyticsTracker.track(
                    MapAnalyticsEvent.MineClicked(
                        college = _collegeId.value,
                        major = _departmentId.value,
                    ),
                )
            }
        }
    }

    // 제휴 정보 로딩
    private fun loadPartnerships() {
        viewModelScope.launch {
            val current = uiState.value
            val currentData = if (current is UiState.Success) current.data else MapState()
            
            _uiState.value = UiState.Loading

            val partnerships = partnershipRepository.getAllPartnerships()
            _uiState.value = UiState.Success(
                currentData.copy(
                    partnerships = partnerships,
                    filterChangeResult = null
                )
            )
        }
    }

    // 사용자 단과대 제휴 정보 로딩
    private fun loadUserCollegePartnerships() {
        viewModelScope.launch {
            val current = uiState.value
            val currentData = if (current is UiState.Success) current.data else MapState()
            
            _uiState.value = UiState.Loading

            val partnerships = partnershipRepository.getUserCollegePartnerships()
            _uiState.value = UiState.Success(
                currentData.copy(
                    partnerships = partnerships,
                    filterChangeResult = null
                )
            )
        }
    }

    fun selectPartnershipByStoreName(storeName: String, partnershipId: Int? = null) {
        val current = uiState.value
        if (current !is UiState.Success) return
        val data = current.data

        // 가게 단위의 Partnership 찾기
        val partnership = data.partnerships.firstOrNull { it.storeName == storeName } ?: return

        val repId = partnershipId ?: partnership.partnershipInfos.firstOrNull()?.id ?: return
        val representative: PartnershipRestaurant =
            getPartnershipDetailUseCase(data.partnerships, storeName, repId)
                ?: return

        // 바텀시트 리스트에 표시할 모든 제휴를 RestaurantInfo로 매핑
        val restaurantInfoList = partnership.partnershipInfos.map { info ->
            RestaurantInfo(
                collegeName = info.collegeName,
                departmentName = info.departmentName,
                period = "${info.startDate} ~ ${info.endDate}",
                benefit = info.description
            )
        }

        _uiState.value = UiState.Success(
            data.copy(
                restaurantPartnershipInfo = representative,
                restaurantInfoList = restaurantInfoList,
                storeType = representative.storeType
            )
        )
    }
}
