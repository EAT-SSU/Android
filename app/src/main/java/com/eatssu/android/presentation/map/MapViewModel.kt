package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.usecase.user.GetPartnershipDetailUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.presentation.map.component.PartnershipCategory
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
    val selectedCategory: PartnershipCategory = PartnershipCategory.ALL,
    val filterChangeResult: FilterChangeResult? = null,
) {
    val visiblePartnerships: List<Partnership>
        get() = selectedCategory.storeType?.let { storeType ->
            partnerships.filter { it.restaurantType == storeType }
        } ?: partnerships

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

    fun refresh() {
        fetchUserCollegeDepartment()
    }

    fun clearFilterChangeResult() {
        val current = uiState.value
        if (current is UiState.Success) {
            _uiState.value = UiState.Success(current.data.copy(filterChangeResult = null))
        }
    }

    private fun fetchUserCollegeDepartment() {
        viewModelScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            val newDepartmentId = userCollegeDepartment.userDepartment.departmentId.toLong()
            val newCollegeId = userCollegeDepartment.userCollege.collegeId.toLong()
            val selectedCategory = (_uiState.value as? UiState.Success)
                ?.data
                ?.selectedCategory
                ?: PartnershipCategory.ALL

            _departmentId.value = newDepartmentId
            _collegeId.value = newCollegeId

            if (newDepartmentId == -1L) {
                // 학과 미입력 유저: 바텀시트 요청 상태 설정, 마커 데이터는 빈 상태(emptyList)로 둠
                _uiState.value = UiState.Success(
                    MapState(
                        selectedCategory = selectedCategory,
                        filterChangeResult = MapState.FilterChangeResult.RequiresDepartment,
                        partnerships = emptyList(),
                    ),
                )
            } else {
                _uiState.value = UiState.Success(
                    MapState(
                        selectedCategory = selectedCategory,
                    ),
                )
                loadUserCollegePartnerships()
            }

            analyticsTracker.track(
                MapAnalyticsEvent.MapClicked(
                    college = _collegeId.value,
                    major = _departmentId.value,
                    isFestival = false,
                ),
            )

            Timber.d("학과 정보 : ${userCollegeDepartment.userDepartment.departmentName}")
        }
    }

    fun setCategory(category: PartnershipCategory) {
        val current = uiState.value as? UiState.Success ?: return
        if (current.data.selectedCategory == category) return

        _uiState.value = UiState.Success(
            current.data.copy(
                selectedCategory = category,
                restaurantPartnershipInfo = null,
                restaurantInfoList = emptyList(),
                storeType = null,
            ),
        )
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
                    filterChangeResult = null,
                ),
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
                storeType = representative.storeType,
            ),
        )
    }

    fun clearSelectedPartnership() {
        val current = uiState.value
        if (current !is UiState.Success) return

        _uiState.value = UiState.Success(
            current.data.copy(
                restaurantPartnershipInfo = null,
                restaurantInfoList = emptyList(),
                storeType = null,
            ),
        )
    }

    fun likePartnership(id: Int) {
        viewModelScope.launch {
            val current = _uiState.value as? UiState.Success ?: return@launch
            val wasLiked = current.data.partnershipLikeStatus(id) ?: return@launch

            // 찜 상태는 즉시 화면에 반영하고, 서버 요청이 실패한 경우에만 원래 상태로 되돌린다.
            _uiState.value = UiState.Success(current.data.togglePartnershipLike(id))

            val result = partnershipRepository.likePartnership(id, wasLiked)
            if (result is ApiResult.Success) return@launch

            val latest = _uiState.value as? UiState.Success ?: return@launch
            if (latest.data.partnershipLikeStatus(id) == !wasLiked) {
                _uiState.value = UiState.Success(latest.data.togglePartnershipLike(id))
            }
        }
    }
}

private fun MapState.partnershipLikeStatus(id: Int): Boolean? =
    restaurantPartnershipInfo
        ?.takeIf { it.id == id }
        ?.likedByUser
        ?: partnerships.asSequence()
            .flatMap { it.partnershipInfos.asSequence() }
            .firstOrNull { it.id == id }
            ?.isLiked

private fun MapState.togglePartnershipLike(id: Int): MapState =
    copy(
        partnerships = partnerships.map { partnership ->
            partnership.copy(
                partnershipInfos = partnership.partnershipInfos.map { info ->
                    if (info.id == id) {
                        info.copy(
                            isLiked = !info.isLiked,
                            likeCount = info.likeCount.updatedLikeCount(info.isLiked),
                        )
                    } else {
                        info
                    }
                },
            )
        },
        restaurantPartnershipInfo = restaurantPartnershipInfo?.let { info ->
            if (info.id == id) {
                info.copy(
                    likedByUser = !info.likedByUser,
                    partnershipLikeCount = info.partnershipLikeCount.updatedLikeCount(info.likedByUser),
                )
            } else {
                info
            }
        },
    )

private fun Int.updatedLikeCount(wasLiked: Boolean): Int =
    (this + if (wasLiked) -1 else 1).coerceAtLeast(0)
