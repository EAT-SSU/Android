package com.eatssu.android.presentation.cafeteria.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetFixedMenuResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MealService
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuService: MenuService,
    private val mealService: MealService,
) :ViewModel() {

    private val _todayMealDataDodam = MutableStateFlow<ArrayList<GetMealResponse>>(arrayListOf())
    val todayMealDataDodam: StateFlow<ArrayList<GetMealResponse>> = _todayMealDataDodam

    private val _todayMealDataHaksik = MutableStateFlow<ArrayList<GetMealResponse>>(arrayListOf())
    val todayMealDataHaksik: StateFlow<ArrayList<GetMealResponse>> = _todayMealDataHaksik

    private val _todayMealDataDormitory =
        MutableStateFlow<ArrayList<GetMealResponse>>(arrayListOf())
    val todayMealDataDormitory: StateFlow<ArrayList<GetMealResponse>> = _todayMealDataDormitory

    private val _fixedMenuDataSnack =
        MutableStateFlow<GetFixedMenuResponse>(GetFixedMenuResponse(arrayListOf()))
    val fixedMenuDataSnack: StateFlow<GetFixedMenuResponse> = _fixedMenuDataSnack

    private val _fixedMenuDataKitchen =
        MutableStateFlow<GetFixedMenuResponse>(GetFixedMenuResponse(arrayListOf()))
    val fixedMenuDataKitchen: StateFlow<GetFixedMenuResponse> = _fixedMenuDataKitchen

    private val _fixedMenuDataFood =
        MutableStateFlow<GetFixedMenuResponse>(GetFixedMenuResponse(arrayListOf()))
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
            mealService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())
                .enqueue(object : Callback<BaseResponse<ArrayList<GetMealResponse>>> {
                    override fun onResponse(
                        call: Call<BaseResponse<ArrayList<GetMealResponse>>>,
                        response: Response<BaseResponse<ArrayList<GetMealResponse>>>,
                    ) {
                        val restaurantMenuData = response.body()?.result ?: arrayListOf()

                        if (response.isSuccessful) {
                            Timber.d("onResponse 성공" + response.body())

                            when (restaurantType) {
                                Restaurant.HAKSIK -> _todayMealDataHaksik.value = restaurantMenuData
                                Restaurant.DODAM -> _todayMealDataDodam.value = restaurantMenuData
                                Restaurant.DORMITORY -> _todayMealDataDormitory.value = restaurantMenuData
                                else -> Timber.d("onResponse 실패. 잘못된 식당입니다.")
                            }
                            _uiState.value = UiState.Success(MenuState())

                        } else {
                            Timber.d("onResponse 실패 투데이밀" + response.code() + response.message())
                            _uiState.value = UiState.Error
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<ArrayList<GetMealResponse>>>,
                        t: Throwable,
                    ) {
                        Timber.d("onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
                        _uiState.value = UiState.Error
                    }
                })
        }
    }

    // Fixed Menu 데이터 로드도 유사한 방식으로 구현
    fun loadFixedMenu(restaurantType: Restaurant) {
        Timber.d("Debug", "loadFixedMenu called with type: $restaurantType")

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            menuService.getFixMenu(restaurantType.toString())
                .enqueue(object : Callback<BaseResponse<GetFixedMenuResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetFixedMenuResponse>>,
                        response: Response<BaseResponse<GetFixedMenuResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            Timber.d("onResponse 성공" + response.body())
                            val data =
                                response.body()?.result ?: GetFixedMenuResponse(arrayListOf())
                            when (restaurantType) {
                                Restaurant.THE_KITCHEN -> _fixedMenuDataKitchen.value = data
                                Restaurant.FOOD_COURT -> _fixedMenuDataFood.value = data
                                Restaurant.SNACK_CORNER -> _fixedMenuDataSnack.value = data

                                else -> {
                                    Timber.d("onResponse 실패. 잘못된 식당 입니다.")
                                }
                            }
                            _uiState.value = UiState.Success(MenuState())
                        } else {
                            Timber.d("onResponse 실패")
                            _uiState.value = UiState.Error
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<GetFixedMenuResponse>>,
                        t: Throwable,
                    ) {
                        Timber.d("onFailure 에러: ${t.message}")
                        _uiState.value = UiState.Error
                    }
                })
        }
    }
}

data class MenuState(
    var haksikMeal: ArrayList<GetMealResponse>? = null,
    var dodamMeal: ArrayList<GetMealResponse>? = null,
    var dormitoryMeal: ArrayList<GetMealResponse>? = null,
    var snackMenu: GetFixedMenuResponse? = null,
    var foodcourtMenu: GetFixedMenuResponse? = null,
    var menuOfMeal: List<MenuMini>? = null,
)