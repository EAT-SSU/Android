package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.usecase.user.GetPartnershipDetailUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.presentation.map.model.RestaurantInfo
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MapState(
    val showDepartmentBottomSheet: Boolean = false,
    val showPartnershipBottomSheet: Boolean = false,
    val partnerships: List<Partnership> = emptyList(),
    val restaurantPartnershipInfo: PartnershipRestaurant? = null,
    val restaurantInfoList: List<RestaurantInfo> = emptyList(),
    var partnershipToggleText: String = "내 제휴",
    val currentCollegeName: String = "",
    val currentDepartmentName: String = "",
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository,
    private val getPartnershipDetailUseCase: GetPartnershipDetailUseCase,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MapState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MapState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    var departmentId: Long = -1
        private set
    var collegeId: Long = -1
        private set

    init {
        fetchUserCollegeDepartment()
        loadPartnerships()
    }

    private fun fetchUserCollegeDepartment() {
        viewModelScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            departmentId = userCollegeDepartment.userDepartment.departmentId.toLong()
            collegeId = userCollegeDepartment.userCollege.collegeId.toLong()
            Timber.d("학과 정보 : ${userCollegeDepartment.userDepartment.departmentName}")
        }
    }

    // 제휴 정보 로딩
    fun loadPartnerships() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val partnerships = partnershipRepository.getAllPartnerships()
            _uiState.value = UiState.Success(MapState(partnerships = partnerships))
        }
    }

    // 사용자 단과대 제휴 정보 로딩
    fun loadUserCollegePartnerships() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val partnerships = partnershipRepository.getUserCollegePartnerships()
            _uiState.value = UiState.Success(MapState(partnerships = partnerships))
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
                showPartnershipBottomSheet = true,
                restaurantPartnershipInfo = representative,
                restaurantInfoList = restaurantInfoList
            )
        )
    }

    // 학과 정보 입력 bottomSheet 보여주기 toggle
    fun toggleDepartmentBottomSheet() {
        val current = uiState.value
        if (current is UiState.Success) {
            current.data.let { data ->
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
            current.data.let { data ->
                _uiState.value = UiState.Success(
                    data.copy(showPartnershipBottomSheet = !data.showPartnershipBottomSheet)
                )
            }
        }
    }
}
