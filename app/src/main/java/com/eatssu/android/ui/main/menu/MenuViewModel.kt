package com.eatssu.android.ui.main.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.dto.response.ChangeMenuInfoListDto
import com.eatssu.android.data.dto.response.GetFixedMenuResponseDto
import com.eatssu.android.data.dto.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuViewModel(private val menuService: MenuService) : ViewModel() {

    private val _todayMealDataDodam = MutableLiveData<GetTodayMealResponseDto>()
    val todayMealDataDodam: LiveData<GetTodayMealResponseDto> = _todayMealDataDodam

    private val _todayMealDataHaksik = MutableLiveData<GetTodayMealResponseDto>()
    val todayMealDataHaksik: LiveData<GetTodayMealResponseDto> = _todayMealDataHaksik

    private val _todayMealDataDormitory = MutableLiveData<GetTodayMealResponseDto>()
    val todayMealDataDormitory: LiveData<GetTodayMealResponseDto> = _todayMealDataDormitory

    private val _fixedMenuDataKitchen = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuDataKitchen: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuDataKitchen

    private val _fixedMenuDataSnack = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuDataSnack: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuDataSnack

    private val _fixedMenuDataFood = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuDataFood: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuDataFood

    private val _menuBymealId = MutableLiveData<ChangeMenuInfoListDto>()
    val menuBymealId: MutableLiveData<ChangeMenuInfoListDto> = _menuBymealId


    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time
    ) {
        viewModelScope.launch {
            menuService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())
                .enqueue(object : Callback<GetTodayMealResponseDto> {
                    override fun onResponse(
                        call: Call<GetTodayMealResponseDto>,
                        response: Response<GetTodayMealResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공" + response.body())

                            when (restaurantType) {
                                Restaurant.HAKSIK -> _todayMealDataHaksik.postValue(response.body())
                                Restaurant.DODAM -> _todayMealDataDodam.postValue(response.body())
                                Restaurant.DORMITORY -> _todayMealDataDormitory.postValue(response.body())

                                else -> {
                                    Log.d("post", "onResponse 실패. 잘못된 식당입니다.")
                                }
                            }
                        } else {
                            Log.d(
                                "post",
                                "onResponse 실패 투데이밀" + response.code() + response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<GetTodayMealResponseDto>, t: Throwable) {
                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
                    }
                })
        }
    }

    // Fixed Menu 데이터 로드도 유사한 방식으로 구현
    fun loadFixedMenu(restaurantType: Restaurant) {
        viewModelScope.launch {
            menuService.getFixMenu(restaurantType.toString())
                .enqueue(object : Callback<GetFixedMenuResponseDto> {
                    override fun onResponse(
                        call: Call<GetFixedMenuResponseDto>,
                        response: Response<GetFixedMenuResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공" + response.body())

                            when (restaurantType) {

                                Restaurant.THE_KITCHEN -> _fixedMenuDataKitchen.postValue(response.body())
                                Restaurant.FOOD_COURT -> _fixedMenuDataFood.postValue(response.body())
                                Restaurant.SNACK_CORNER -> _fixedMenuDataSnack.postValue(response.body())

                                else -> {
                                    Log.d("post", "onResponse 실패. 잘못된 식당 입니다.")
                                }
                            }
                        } else {
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<GetFixedMenuResponseDto>, t: Throwable) {
                        Log.d("post", "onFailure 에러: ${t.message}")
                    }
                })
        }
    }

    fun findMenuItemByMealId(mealId: Long) {
        viewModelScope.launch {
            menuService.getMenuByMealId(mealId)
                .enqueue(object : Callback<ChangeMenuInfoListDto> {
                    override fun onResponse(
                        call: Call<ChangeMenuInfoListDto>,
                        response: Response<ChangeMenuInfoListDto>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공" + response.body())

                            menuBymealId.postValue(response.body())

                        } else {
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<ChangeMenuInfoListDto>, t: Throwable) {
                        Log.d("post", "onFailure 에러: ${t.message}")
                    }
                })
        }
    }
}