package com.eatssu.android.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.common.UiState
import com.eatssu.common.enums.StoreType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritePartnershipItem(
    val partnershipId: Int,
    val storeName: String,
    val storeType: StoreType,
    val description: String,
    val detail: PartnershipRestaurant? = null,
)

data class FavoriteState(
    val partnerships: List<FavoritePartnershipItem> = emptyList(),
    val selectedStoreType: StoreType? = null,
) {
    val filteredPartnerships: List<FavoritePartnershipItem>
        get() = selectedStoreType?.let { type ->
            partnerships.filter { it.storeType == type }
        } ?: partnerships
}

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository,
    private val favoritePartnershipDataStore: FavoritePartnershipDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<FavoriteState>>(UiState.Init)
    val uiState: StateFlow<UiState<FavoriteState>> = _uiState.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            val selectedStoreType = (_uiState.value as? UiState.Success)?.data?.selectedStoreType
            _uiState.value = UiState.Loading

            val items = partnershipRepository.getUserFavoritePartnerships()
                .mapNotNull(Partnership::toFavoriteItemOrNull)
                .distinctBy { it.partnershipId }
            val order =
                favoritePartnershipDataStore.reconcile(items.map { it.partnershipId }).distinct()
            val itemById = items.associateBy { it.partnershipId }

            // 서버 목록을 기기에 기록한 최근 찜 순서에 맞춰 정렬한다.
            _uiState.value = UiState.Success(
                FavoriteState(
                    partnerships = order.mapNotNull(itemById::get),
                    selectedStoreType = selectedStoreType,
                ),
            )
        }
    }

    fun selectStoreType(storeType: StoreType?) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = UiState.Success(current.data.copy(selectedStoreType = storeType))
    }

    fun removeFavorite(partnershipId: Int) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            val result = partnershipRepository.likePartnership(partnershipId, wasLiked = true)
            if (result is ApiResult.Success) {
                val updatedPartnerships =
                    current.partnerships.filterNot { it.partnershipId == partnershipId }
                _uiState.value = UiState.Success(current.copy(partnerships = updatedPartnerships))
            }
        }
    }

    fun removeFavorites(partnershipIds: Set<Int>) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (partnershipIds.isEmpty()) return

        viewModelScope.launch {
            val jobs = partnershipIds.map { id ->
                async {
                    val result = partnershipRepository.likePartnership(id, wasLiked = true)
                    if (result is ApiResult.Success) id else null
                }
            }
            val successfulIds = jobs.awaitAll().filterNotNull().toSet()
            if (successfulIds.isNotEmpty()) {
                val updatedPartnerships =
                    current.partnerships.filterNot { it.partnershipId in successfulIds }
                _uiState.value = UiState.Success(current.copy(partnerships = updatedPartnerships))
            }
        }
    }

    fun restoreFavorites(items: List<FavoritePartnershipItem>) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (items.isEmpty()) return

        viewModelScope.launch {
            val jobs = items.map { item ->
                async {
                    val result =
                        partnershipRepository.likePartnership(item.partnershipId, wasLiked = false)
                    if (result is ApiResult.Success) item else null
                }
            }
            val restoredItems = jobs.awaitAll().filterNotNull()
            if (restoredItems.isNotEmpty()) {
                val existingIds = current.partnerships.map { it.partnershipId }.toSet()
                val newlyAdded = restoredItems.filterNot { it.partnershipId in existingIds }
                val updated = newlyAdded + current.partnerships
                _uiState.value = UiState.Success(current.copy(partnerships = updated))
            }
        }
    }
}

private fun Partnership.toFavoriteItemOrNull(): FavoritePartnershipItem? {
    val representative = partnershipInfos.firstOrNull { it.isLiked }
        ?: partnershipInfos.firstOrNull()
        ?: return null

    return FavoritePartnershipItem(
        partnershipId = representative.id,
        storeName = storeName,
        storeType = restaurantType,
        description = representative.description,
        detail = PartnershipRestaurant(
            id = representative.id,
            partnershipType = representative.partnershipType,
            storeName = storeName,
            description = representative.description,
            startDate = representative.startDate,
            endDate = representative.endDate,
            storeType = restaurantType,
            longitude = longitude,
            latitude = latitude,
            collegeName = representative.collegeName,
            departmentName = representative.departmentName,
            partnershipLikeCount = representative.likeCount,
            likedByUser = representative.isLiked,
            naverMapUrl = naverMapUrl,
            kakaoMapUrl = kakaoMapUrl,
        ),
    )
}
