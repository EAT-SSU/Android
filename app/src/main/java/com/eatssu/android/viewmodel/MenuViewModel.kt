package com.eatssu.android.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.repository.MenuRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// MenuViewModel.kt
class MenuViewModel(private val repository: MenuRepository) : ViewModel() {

    private val _todayMealData = MutableLiveData<GetTodayMealResponseDto>()
    val todayMealData: LiveData<GetTodayMealResponseDto> = _todayMealData
//    val todayMealData: LiveData<GetTodayMealResponseDto>
//        get() = _todayMealData


    private val _fixedMenuData = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuData: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuData
//    val fixedMenuData: MutableLiveData<GetFixedMenuResponseDto>
//        get() = _fixedMenuData


    private val _fixedMenuDataKitchen = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuDataKitchen: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuDataKitchen
//    val fixedMenuDataKitchen: MutableLiveData<GetFixedMenuResponseDto>
//        get() = _fixedMenuDataKitchen


    private val _fixedMenuDataSnack = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuDataSnack: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuDataSnack

    private val _fixedMenuDataFood = MutableLiveData<GetFixedMenuResponseDto>()
    val fixedMenuDataFood: MutableLiveData<GetFixedMenuResponseDto> = _fixedMenuDataFood

    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time
    ) {
        viewModelScope.launch {
            repository.getTodayMeal(menuDate, restaurantType, time)
                .enqueue(object : Callback<GetTodayMealResponseDto> {
                    override fun onResponse(
                        call: Call<GetTodayMealResponseDto>,
                        response: Response<GetTodayMealResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공 투데이밀" + response.body())
                            _todayMealData.postValue(response.body())
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
            repository.getFixedMenu(restaurantType)
                .enqueue(object : Callback<GetFixedMenuResponseDto> {
                    override fun onResponse(
                        call: Call<GetFixedMenuResponseDto>,
                        response: Response<GetFixedMenuResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            when (restaurantType) {
                                Restaurant.THE_KITCHEN -> _fixedMenuDataKitchen.postValue(response.body())
                                Restaurant.FOOD_COURT -> _fixedMenuDataFood.postValue(response.body())
                                Restaurant.SNACK_CORNER -> _fixedMenuDataSnack.postValue(response.body())

                                else -> {
                                    Log.d("post", "onResponse 실패")
                                }
                            }
//                            _fixedMenuData.postValue(response.body())
                            Log.d("post", "onResponse 성공" + response.body())

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
}