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
import com.eatssu.common.EventLogger
import com.eatssu.common.UiEvent
import com.eatssu.common.enums.StoreType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

sealed interface MapUiState {
    data object Loading : MapUiState
}

data class MapState(
    val partnerships: List<Partnership> = emptyList(),
    val restaurantPartnershipInfo: PartnershipRestaurant? = null,
    val restaurantInfoList: List<RestaurantInfo> = emptyList(),
    val storeType: StoreType? = null,
    val selectedFilter: FilterType = FilterType.Mine,
    val departmentName: String = "학과",
    val showUserDepartmentBottomSheet: Boolean = false,
    val filterChangeResult: FilterChangeResult? = null,
) : MapUiState {
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
) : ViewModel() {

    private val _uiState: MutableStateFlow<MapUiState> = MutableStateFlow(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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

            val initialFilter = if (newDepartmentId == -1L) FilterType.All else FilterType.Mine

            _uiState.value = MapState(
                selectedFilter = initialFilter,
                departmentName = userCollegeDepartment.userDepartment.departmentName,
                showUserDepartmentBottomSheet = (newCollegeId == -1L || newDepartmentId == -1L)
            )

            // 초기 필터에 따라 데이터 로드
            when (initialFilter) {
                FilterType.All -> loadPartnerships()
                FilterType.Mine -> loadUserCollegePartnerships()
            }
            
            Timber.d("학과 정보 : ${userCollegeDepartment.userDepartment.departmentName}")
        }
    }

    fun refreshUserDepartment() {
        viewModelScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            val newDepartmentId = userCollegeDepartment.userDepartment.departmentId.toLong()
            val newCollegeId = userCollegeDepartment.userCollege.collegeId.toLong()

            _departmentId.value = newDepartmentId
            _collegeId.value = newCollegeId

            val currentData = (_uiState.value as? MapState) ?: MapState()
            val nextSelectedFilter =
                if (newDepartmentId == -1L && currentData.selectedFilter == FilterType.Mine) {
                    FilterType.All
                } else {
                    currentData.selectedFilter
                }

            _uiState.value = currentData.copy(
                selectedFilter = nextSelectedFilter,
                departmentName = userCollegeDepartment.userDepartment.departmentName,
                showUserDepartmentBottomSheet = (newCollegeId == -1L || newDepartmentId == -1L),
            )

            if (currentData.selectedFilter == FilterType.Mine && nextSelectedFilter == FilterType.All) {
                loadPartnerships()
            }
        }
    }

    // 필터 변경 (검증 로직 포함)
    fun setFilter(filter: FilterType) {
        val currentData = (_uiState.value as? MapState) ?: return

        if (currentData.selectedFilter == filter) return

        // 학과 정보가 없는데 Mine 필터를 선택하려는 경우
        if (filter == FilterType.Mine && _departmentId.value == -1L) {
            // 학과 입력이 필요한 경우 결과를 MapState에 반영
            _uiState.value = currentData.copy(filterChangeResult = MapState.FilterChangeResult.RequiresDepartment)
            return
        }

        // 필터 변경 성공
        val updatedData = currentData.copy(
            restaurantPartnershipInfo = null,
            selectedFilter = filter,
            filterChangeResult = null,
        )
        _uiState.value = updatedData

        // 필터에 따라 데이터 로드
        when (filter) {
            FilterType.All -> {
                loadPartnerships()
                EventLogger.clickMap()
            }

            FilterType.Mine -> {
                loadUserCollegePartnerships()
                EventLogger.clickMapMine(_collegeId.value, _departmentId.value)
            }
        }
    }

    // 제휴 정보 로딩
    private fun loadPartnerships() {
        viewModelScope.launch {
            val currentData = (_uiState.value as? MapState) ?: MapState()

            val partnerships = partnershipRepository.getAllPartnerships()
            _uiState.value = currentData.copy(
                partnerships = partnerships,
                filterChangeResult = null
            )
        }
    }

    // 사용자 단과대 제휴 정보 로딩
    private fun loadUserCollegePartnerships() {
        viewModelScope.launch {
            val currentData = (_uiState.value as? MapState) ?: MapState()

            val partnerships = partnershipRepository.getUserCollegePartnerships()
            _uiState.value = currentData.copy(
                partnerships = partnerships,
                filterChangeResult = null
            )
        }
    }

    fun selectPartnershipByStoreName(storeName: String, partnershipId: Int? = null) {
        if (_uiState.value !is MapState) return
        val data = _uiState.value as MapState

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

        _uiState.value = data.copy(
            restaurantPartnershipInfo = representative,
            restaurantInfoList = restaurantInfoList,
            storeType = representative.storeType
        )
    }
}
