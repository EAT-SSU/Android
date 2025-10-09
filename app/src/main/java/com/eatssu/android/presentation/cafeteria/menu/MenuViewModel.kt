package com.eatssu.android.presentation.cafeteria.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.GetFixedMenuResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.service.MealService
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.domain.model.MenuMini
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

    private val _todayMealDataDodam = MutableStateFlow<List<GetMealResponse>>(emptyList())
    val todayMealDataDodam: StateFlow<List<GetMealResponse>> = _todayMealDataDodam

    private val _todayMealDataHaksik = MutableStateFlow<List<GetMealResponse>>(emptyList())
    val todayMealDataHaksik: StateFlow<List<GetMealResponse>> = _todayMealDataHaksik

    private val _todayMealDataDormitory =
        MutableStateFlow<List<GetMealResponse>>(emptyList())
    val todayMealDataDormitory: StateFlow<List<GetMealResponse>> = _todayMealDataDormitory

    private val _todayMealDataFaculty =
        MutableStateFlow<List<GetMealResponse>>(emptyList())
    val todayMealDataFaculty: StateFlow<List<GetMealResponse>> = _todayMealDataFaculty

    private val _fixedMenuDataSnack =
        MutableStateFlow<GetFixedMenuResponse>(GetFixedMenuResponse(emptyList()))
    val fixedMenuDataSnack: StateFlow<GetFixedMenuResponse> = _fixedMenuDataSnack

    private val _fixedMenuDataKitchen =
        MutableStateFlow<GetFixedMenuResponse>(GetFixedMenuResponse(emptyList()))
    val fixedMenuDataKitchen: StateFlow<GetFixedMenuResponse> = _fixedMenuDataKitchen

    private val _fixedMenuDataFood =
        MutableStateFlow<GetFixedMenuResponse>(GetFixedMenuResponse(emptyList()))
    val fixedMenuDataFood: StateFlow<GetFixedMenuResponse> = _fixedMenuDataFood

    private val _uiState: MutableStateFlow<UiState<MenuState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MenuState>> = _uiState.asStateFlow()


    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time,
    ) {
        _uiState.value = UiState.Loading
        Timber.d("Debug", "loadTodayMeal called with type: $restaurantType")

        viewModelScope.launch {
            when (val result =
                mealService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())) {
                is ApiResult.Success -> {
                    val restaurantMenuData = result.data
                    when (restaurantType) {
                        Restaurant.HAKSIK -> _todayMealDataHaksik.value = restaurantMenuData
                        Restaurant.DODAM -> _todayMealDataDodam.value = restaurantMenuData
                        Restaurant.DORMITORY -> _todayMealDataDormitory.value = restaurantMenuData
                        Restaurant.FACULTY -> _todayMealDataFaculty.value = restaurantMenuData
                        else -> Timber.d("onResponse 실패. 잘못된 식당입니다.")
                    }
                    _uiState.value = UiState.Success(MenuState())
                }

                else -> {
                    _uiState.value = UiState.Error
                }
            }
        }
    }

    // Fixed Menu 데이터 로드도 유사한 방식으로 구현
    fun loadFixedMenu(restaurantType: Restaurant) {
        Timber.d("Debug", "loadFixedMenu called with type: $restaurantType")

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            when (val result = menuService.getFixMenu(restaurantType.toString())) {
                is ApiResult.Success -> {
                    Timber.d("onResponse 성공: ${result.data}")
                    val fixMenuData = result.data

                    when (restaurantType) {
                        Restaurant.THE_KITCHEN -> _fixedMenuDataKitchen.value = fixMenuData
                        Restaurant.FOOD_COURT -> _fixedMenuDataFood.value = fixMenuData
                        Restaurant.SNACK_CORNER -> _fixedMenuDataSnack.value = fixMenuData
                        else -> Timber.d("잘못된 식당입니다.")
                    }
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
    var haksikMeal: List<GetMealResponse>? = null,
    var dodamMeal: List<GetMealResponse>? = null,
    var dormitoryMeal: List<GetMealResponse>? = null,
    var snackMenu: GetFixedMenuResponse? = null,
    var foodcourtMenu: GetFixedMenuResponse? = null,
    var menuOfMeal: List<MenuMini>? = null,
)