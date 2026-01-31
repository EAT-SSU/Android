package com.eatssu.android.presentation.cafeteria.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.usecase.menu.GetMenuListUseCase
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getMenuListUseCase: GetMenuListUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MenuState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MenuState>> = _uiState.asStateFlow()

    // 주어진 식당 리스트에 대해 메뉴 정보를 비동기로 가져와서 UI 상태를 업데이트
    fun loadMenus(restaurants: List<Restaurant>, menuDate: String, time: Time) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            // async 함수로 Deferred를 만들어 메뉴 정보 한번에 가져오기
            val deferredMenus = restaurants.map { restaurant ->
                async {
                    restaurant to getMenuListUseCase(restaurant, menuDate, time)
                }
            }

            val menuMap = deferredMenus.awaitAll().toMap()
            _uiState.value = UiState.Success(MenuState(menuMap))
        }
    }
}

data class MenuState(
    val menuMap: Map<Restaurant, List<Menu>> = emptyMap()
)