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
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.map.model.RestaurantInfo
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.MapAnalyticsEvent
import com.eatssu.common.enums.PeriodType
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
    val availableFilters: List<FilterType> = FilterType.entries,
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
            // Festival 존재 여부를 판단하고, All/Festival 초기 목록에도 재사용한다.
            val allPartnerships = partnershipRepository.getAllPartnerships()

            _departmentId.value = newDepartmentId
            _collegeId.value = newCollegeId

            val hasFestival = allPartnerships.hasFestivalPartnership()
            val availableFilters = if (hasFestival) {
                FilterType.entries
            } else {
                listOf(FilterType.Mine, FilterType.All)
            }

            // Festival 제휴가 하나라도 있으면 Festival을 우선하고, 없으면 Mine을 기본으로 설정한다.
            val initialFilter = when {
                hasFestival -> FilterType.Festival
                else -> FilterType.Mine
            }

            if (newDepartmentId == -1L && initialFilter == FilterType.Mine) {
                // 학과 미입력 유저: 바텀시트 요청 상태 설정, 마커 데이터는 빈 상태(emptyList)로 둠
                _uiState.value = UiState.Success(
                    MapState(
                        selectedFilter = FilterType.Mine,
                        availableFilters = availableFilters,
                        filterChangeResult = MapState.FilterChangeResult.RequiresDepartment,
                        partnerships = emptyList(),
                    ),
                )
            } else {
                _uiState.value = UiState.Success(
                    MapState(
                        selectedFilter = initialFilter,
                        availableFilters = availableFilters,
                    ),
                )

                when (initialFilter) {
                    FilterType.All -> loadPartnerships(prefetchedPartnerships = allPartnerships)
                    FilterType.Festival -> loadFestivalPartnerships(prefetchedPartnerships = allPartnerships)
                    FilterType.Mine -> loadUserCollegePartnerships()
                }
            }

            analyticsTracker.track(
                MapAnalyticsEvent.MapClicked(
                    college = _collegeId.value,
                    major = _departmentId.value,
                    isFestival = (initialFilter == FilterType.Festival)
                ),
            )

            Timber.d("학과 정보 : ${userCollegeDepartment.userDepartment.departmentName}")
        }
    }

    // 필터 변경 (검증 로직 포함)
    fun setFilter(filter: FilterType) {
        val current = uiState.value
        val currentData = if (current is UiState.Success) current.data else MapState()

        // 학과 정보가 없는데 Mine 필터를 선택하려는 경우
        if (filter == FilterType.Mine && _departmentId.value == -1L) {
            // 학과 입력이 필요한 경우 결과를 MapState에 반영하고 마커는 비어있는 상태 유지
            if (current is UiState.Success) {
                _uiState.value = UiState.Success(
                    currentData.copy(
                        selectedFilter = FilterType.Mine,
                        filterChangeResult = MapState.FilterChangeResult.RequiresDepartment,
                        partnerships = emptyList(),
                    )
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
                analyticsTracker.track(
                    MapAnalyticsEvent.AllClicked(
                        college = _collegeId.value,
                        major = _departmentId.value,
                    ),
                )
            }

            FilterType.Festival -> {
                loadFestivalPartnerships()
                analyticsTracker.track(
                    MapAnalyticsEvent.FestivalClicked(
                        college = _collegeId.value,
                        major = _departmentId.value,
                    ),
                )
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
    private fun loadPartnerships(
        prefetchedPartnerships: List<Partnership>? = null,
    ) {
        viewModelScope.launch {
            val current = uiState.value
            val currentData = if (current is UiState.Success) current.data else MapState()

            if (prefetchedPartnerships == null)
                _uiState.value = UiState.Loading

            val partnerships = prefetchedPartnerships ?: partnershipRepository.getAllPartnerships()
            _uiState.value = UiState.Success(
                currentData.copy(
                    partnerships = partnerships,
                    filterChangeResult = null,
                ),
            )
        }
    }

    private fun loadFestivalPartnerships(
        prefetchedPartnerships: List<Partnership>? = null,
    ) {
        viewModelScope.launch {
            val current = uiState.value
            val currentData = if (current is UiState.Success) current.data else MapState()

            if (prefetchedPartnerships == null)
                _uiState.value = UiState.Loading

            val partnerships =
                (prefetchedPartnerships ?: partnershipRepository.getAllPartnerships())
                    .festivalPartnerships()

            _uiState.value = UiState.Success(
                currentData.copy(
                    partnerships = partnerships,
                    filterChangeResult = null,
                ),
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
            val result = partnershipRepository.likePartnership(id, wasLiked)
            if (result !is ApiResult.Success) return@launch

            val latest = _uiState.value as? UiState.Success ?: return@launch

            // 서버 반영에 성공하면 기존 목록과 현재 열린 상세의 좋아요 상태를 함께 갱신한다.
            _uiState.value = UiState.Success(latest.data.togglePartnershipLike(id))
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

private fun List<Partnership>.hasFestivalPartnership(): Boolean =
    any { partnership ->
        partnership.partnershipInfos.any { info -> info.periodType == PeriodType.FESTIVAL }
    }

private fun List<Partnership>.festivalPartnerships(): List<Partnership> =
    mapNotNull { partnership ->
        val festivalInfos =
            partnership.partnershipInfos.filter { info -> info.periodType == PeriodType.FESTIVAL }
        if (festivalInfos.isEmpty()) return@mapNotNull null

        partnership.copy(partnershipInfos = festivalInfos)
    }
