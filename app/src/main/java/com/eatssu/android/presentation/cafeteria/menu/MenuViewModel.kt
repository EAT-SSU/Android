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
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    private val _uiState: MutableStateFlow<UiState<MenuState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MenuState>> = _uiState.asStateFlow()

    fun loadMenus(restaurants: List<Restaurant>, menuDate: String, time: Time) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val deferredMenus = restaurants.map { restaurant ->
                async {
                    when (restaurant.menuType) {
                        MenuType.FIXED -> {
                            when (val result = menuService.getFixMenu(restaurant.toString())) {
                                is ApiResult.Success -> {
                                    restaurant to result.data.mapFixedMenuResponseToMenu()
                                }

                                else -> {
                                    Timber.e("Failed to load fixed menu for $restaurant")
                                    restaurant to emptyList()
                                }
                            }
                        }

                        MenuType.VARIABLE -> {
                            when (val result = mealService.getTodayMeal(
                                menuDate,
                                restaurant.toString(),
                                time.toString()
                            )) {
                                is ApiResult.Success -> {
                                    restaurant to result.data.mapTodayMenuResponseToMenu()
                                }

                                else -> {
                                    Timber.e("Failed to load meal for $restaurant")
                                    restaurant to emptyList()
                                }
                            }
                        }
                    }
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