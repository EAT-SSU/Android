package com.eatssu.android.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MapState(
    val showBottomSheet: Boolean = false,
    val partnerships: List<Partnership> = emptyList()
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val partnershipRepository: PartnershipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        checkUserDepartment()
        loadPartnerships()

    }

    private fun checkUserDepartment() {
        viewModelScope.launch {
            runCatching {
                userRepository.checkUserDepartment()
            }.onSuccess { hasDepartment ->
                Timber.d("checkUserDepartment: $hasDepartment")
                _uiState.update { it.copy(showBottomSheet = !hasDepartment)
                }
            }.onFailure {
                Timber.e("Error checkUserDepartment: ${it.message}")
            }
        }
    }

    private fun loadPartnerships() {
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

    fun dismissBottomSheet() {
        _uiState.update { it.copy(showBottomSheet = false) }
    }
}
