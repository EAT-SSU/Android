package com.eatssu.android.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteDetailViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<PartnershipRestaurant>>(UiState.Init)
    val uiState: StateFlow<UiState<PartnershipRestaurant>> = _uiState.asStateFlow()

    fun showPartnership(partnership: PartnershipRestaurant) {
        // 찜 목록에서 받은 데이터를 재사용해 상세 진입 시 서버를 다시 조회하지 않는다.
        _uiState.value = UiState.Success(partnership)
    }

    fun loadPartnership(partnershipId: Int) {
        if ((_uiState.value as? UiState.Success)?.data?.id == partnershipId) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = partnershipRepository.getPartnershipById(partnershipId)
                ?.let { UiState.Success(it) }
                ?: UiState.Error
        }
    }

    fun toggleLike() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            val result = partnershipRepository.likePartnership(current.id, current.likedByUser)
            if (result is ApiResult.Success) {
                _uiState.value = UiState.Success(
                    current.copy(
                        likedByUser = !current.likedByUser,
                        partnershipLikeCount = (
                                current.partnershipLikeCount + if (current.likedByUser) -1 else 1
                                ).coerceAtLeast(0),
                    ),
                )
            }
        }
    }
}
