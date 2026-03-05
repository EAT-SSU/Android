package com.eatssu.android.presentation.cafeteria.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.usecase.menu.LoadMenusUseCase
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val loadMenusUseCase: LoadMenusUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MenuState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MenuState>> = _uiState.asStateFlow()

    fun loadMenus(date: LocalDate, time: Time) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val result = loadMenusUseCase(date, time)
            _uiState.value = UiState.Success(
                MenuState(
                    menuMap = result.menuMap,
                    publicHolidayName = result.publicHolidayName,
                )
            )
        }
    }
}

data class MenuState(
    val menuMap: Map<Restaurant, List<Menu>> = emptyMap(),
    val publicHolidayName: String? = null,
)
