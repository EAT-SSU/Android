package com.eatssu.android.ui.main.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuResponse
import com.eatssu.android.data.dto.response.mapFixedMenuResponseToMenu
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.Menu
import com.eatssu.android.data.model.MenuMini
import com.eatssu.android.data.usecase.GetMealUseCase
import com.eatssu.android.data.usecase.GetMenuUseCase
import com.eatssu.android.ui.mypage.MyPageViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getMealUsecase: GetMealUseCase,
    private val getMenuUseCase: GetMenuUseCase,
) :
    ViewModel() {


    private val _todayMealDataDodam = MutableLiveData<ArrayList<GetMealResponse>>()
    val todayMealDataDodam: LiveData<ArrayList<GetMealResponse>> = _todayMealDataDodam

    private val _todayMealDataHaksik = MutableLiveData<ArrayList<GetMealResponse>>()
    val todayMealDataHaksik: LiveData<ArrayList<GetMealResponse>> = _todayMealDataHaksik

    private val _todayMealDataDormitory = MutableLiveData<ArrayList<GetMealResponse>>()
    val todayMealDataDormitory: LiveData<ArrayList<GetMealResponse>> = _todayMealDataDormitory

    private val _fixedMenuDataKitchen = MutableLiveData<MenuResponse>()
    val fixedMenuDataKitchen: MutableLiveData<MenuResponse> = _fixedMenuDataKitchen

    private val _fixedMenuDataSnack = MutableLiveData<MenuResponse>()
    val fixedMenuDataSnack: MutableLiveData<MenuResponse> = _fixedMenuDataSnack

    private val _fixedMenuDataFood = MutableLiveData<MenuResponse>()
    val fixedMenuDataFood: MutableLiveData<MenuResponse> = _fixedMenuDataFood

    private val _uiState: MutableStateFlow<MenuState> = MutableStateFlow(MenuState())
    val uiState: StateFlow<MenuState> = _uiState.asStateFlow()


    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time,
    ) {
        viewModelScope.launch {
            getMenuUseCase(restaurantType.toString()).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Log.e(MyPageViewModel.TAG, e.toString())
            }.collectLatest { result ->
                Log.d(MyPageViewModel.TAG, result.toString())
            }
        }

//                    mealService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())
//                .enqueue(object : Callback<BaseResponse<ArrayList<GetMealResponse>>> {
//                    override fun onResponse(
//                        call: Call<BaseResponse<ArrayList<GetMealResponse>>>,
//                        response: Response<BaseResponse<ArrayList<GetMealResponse>>>,
//                    ) {
//                        val data = response.body()?.result
//
//                        if (response.isSuccessful) {
//                            Log.d("post", "onResponse 성공" + response.body())
//
//                            when (restaurantType) {
//                                Restaurant.HAKSIK -> _todayMealDataHaksik.postValue(data!!)
//                                Restaurant.DODAM -> _todayMealDataDodam.postValue(data!!)
//                                Restaurant.DORMITORY -> _todayMealDataDormitory.postValue(data!!)
//
//                                else -> {
//                                    Log.d("post", "onResponse 실패. 잘못된 식당입니다.")
//                                }
//                            }
//                        } else {
//                            Log.d(
//                                "post",
//                                "onResponse 실패 투데이밀" + response.code() + response.message()
//                            )
//                        }
//                    }
//
//                    override fun onFailure(
//                        call: Call<BaseResponse<ArrayList<GetMealResponse>>>,
//                        t: Throwable,
//                    ) {
//                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
//                    }
//                })
//        }
    }

    // Fixed Menu 데이터 로드도 유사한 방식으로 구현
    fun loadFixedMenu(restaurantType: Restaurant) {
        viewModelScope.launch {
            getMenuUseCase(restaurantType.toString()).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Log.e(TAG, e.toString())
            }.collectLatest { result ->
                result.result?.apply {
                    Log.d(TAG, result.toString())

                    when (restaurantType) {
//                    Restaurant.THE_KITCHEN -> _uiState.update { it.copy(ki = add) }
                        Restaurant.FOOD_COURT -> _uiState.update { it.copy(foodCourtMenu = mapFixedMenuResponseToMenu()) }
                        Restaurant.SNACK_CORNER -> _uiState.update { it.copy(snackMenu = mapFixedMenuResponseToMenu()) }
                        else -> {
                            Log.d("post", "onResponse 실패. 잘못된 식당 입니다.")
                        }
                    }

                }
            }
        }


//        viewModelScope.launch {
//            menuService.getFixMenu(restaurantType.toString())
//                .enqueue(object : Callback<BaseResponse<MenuResponse>> {
//                    override fun onResponse(
//                        call: Call<BaseResponse<MenuResponse>>,
//                        response: Response<BaseResponse<MenuResponse>>,
//                    ) {
//                        if (response.isSuccessful) {
//                            Log.d("post", "onResponse 성공" + response.body())
//                            val data = response.body()?.result!!
//                            when (restaurantType) {
//                                Restaurant.THE_KITCHEN -> _fixedMenuDataKitchen.postValue(data)
//                                Restaurant.FOOD_COURT -> _fixedMenuDataFood.postValue(data)
//                                Restaurant.SNACK_CORNER -> _fixedMenuDataSnack.postValue(data)
//
//                                else -> {
//                                    Log.d("post", "onResponse 실패. 잘못된 식당 입니다.")
//                                }
//                            }
//                        } else {
//                            Log.d("post", "onResponse 실패")
//                        }
//                    }
//
//                    override fun onFailure(
//                        call: Call<BaseResponse<MenuResponse>>,
//                        t: Throwable,
//                    ) {
//                        Log.d("post", "onFailure 에러: ${t.message}")
//                    }
//                })
//        }
    }

    fun findMenuItemByMealId(mealId: Long) {
        viewModelScope.launch {
//            mealService.getMenuInfoByMealId(mealId)
//                .enqueue(object : Callback<BaseResponse<MenuOfMealResponse>> {
//                    override fun onResponse(
//                        call: Call<BaseResponse<MenuOfMealResponse>>,
//                        response: Response<BaseResponse<MenuOfMealResponse>>,
//                    ) {
//                        if (response.isSuccessful) {
//                            val data = response.body()?.result
//                            Log.d("post", "onResponse 성공" + response.body())
//                            _uiState.update {
//                                it.copy(
//                                    menuOfMeal = response.body()?.result?.asMenuOfMeal()
//                                )
//                            }
//                        } else {
//                            Log.d("post", "onResponse 실패")
//                        }
//                    }
//
//                    override fun onFailure(
//                        call: Call<BaseResponse<MenuOfMealResponse>>,
//                        t: Throwable,
//                    ) {
//                        Log.d("post", "onFailure 에러: ${t.message}")
//                    }
//                })
//        }
        }
    }

    companion object {
        val TAG = "MenuViewModel"
    }

}

data class MenuState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,

    var haksikMeal: ArrayList<GetMealResponse>? = null,
    var dodamMeal: ArrayList<GetMealResponse>? = null,
    var dormitoryMeal: List<Menu>? = null,
    var snackMenu: List<Menu>? = null,
    var foodCourtMenu: List<Menu>? = null,

    var menuOfMeal: List<MenuMini>? = null,

    var menuList: List<List<Menu>>? = null,
    var mealList: List<List<Menu>>? = null,
    var totalList: List<List<Menu>>? = null,

    )