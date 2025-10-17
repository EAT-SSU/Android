package com.eatssu.android.presentation.cafeteria.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.mapFixedMenuResponseToMenu
import com.eatssu.android.data.dto.response.mapTodayMenuResponseToMenu
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.service.MealService
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.presentation.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuService: MenuService,
    private val mealService: MealService,
) : ViewModel() {

    private val _menuData = MutableStateFlow<Map<Restaurant, List<Menu>>>(emptyMap())
    val menuData: StateFlow<Map<Restaurant, List<Menu>>> = _menuData.asStateFlow()

    private val _uiState: MutableStateFlow<UiState<MenuState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MenuState>> = _uiState.asStateFlow()


    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time,
    ) {
        _uiState.value = UiState.Loading
        Timber.d("loadTodayMeal called with type: $restaurantType")

        viewModelScope.launch {
            when (val result =
                mealService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())) {
                is ApiResult.Success -> {
                    val menuList = result.data.mapTodayMenuResponseToMenu()
                    _menuData.value = _menuData.value + (restaurantType to menuList)
                    _uiState.value = UiState.Success(MenuState())
                }

                else -> {
                    _uiState.value = UiState.Error
                }
            }
        }
    }

    fun loadFixedMenu(restaurantType: Restaurant) {
        _uiState.value = UiState.Loading
        Timber.d("loadFixedMenu called with type: $restaurantType")

        viewModelScope.launch {
            when (val result = menuService.getFixMenu(restaurantType.toString())) {
                is ApiResult.Success -> {
                    Timber.d("onResponse 성공: ${result.data}")
                    val menuList = result.data.mapFixedMenuResponseToMenu()
                    _menuData.value = _menuData.value + (restaurantType to menuList)
                    _uiState.value = UiState.Success(MenuState())
                }

                else -> {
                    _uiState.value = UiState.Error
                }
            }
        }
    }
}

data class MenuState(
    val message: String = ""
)