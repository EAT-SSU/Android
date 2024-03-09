package com.eatssu.android.ui.main.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ChangeMenuInfoListDto
import com.eatssu.android.data.dto.response.GetFixedMenuResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MealService
import com.eatssu.android.data.service.MenuService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuViewModel(private val menuService: MenuService, private val mealService: MealService) :
    ViewModel() {

    private val _todayMealDataDodam = MutableLiveData<ArrayList<GetMealResponse>>()
    val todayMealDataDodam: LiveData<ArrayList<GetMealResponse>> = _todayMealDataDodam

    private val _todayMealDataHaksik = MutableLiveData<ArrayList<GetMealResponse>>()
    val todayMealDataHaksik: LiveData<ArrayList<GetMealResponse>> = _todayMealDataHaksik

    private val _todayMealDataDormitory = MutableLiveData<ArrayList<GetMealResponse>>()
    val todayMealDataDormitory: LiveData<ArrayList<GetMealResponse>> = _todayMealDataDormitory

    private val _fixedMenuDataKitchen = MutableLiveData<GetFixedMenuResponse>()
    val fixedMenuDataKitchen: MutableLiveData<GetFixedMenuResponse> = _fixedMenuDataKitchen

    private val _fixedMenuDataSnack = MutableLiveData<GetFixedMenuResponse>()
    val fixedMenuDataSnack: MutableLiveData<GetFixedMenuResponse> = _fixedMenuDataSnack

    private val _fixedMenuDataFood = MutableLiveData<GetFixedMenuResponse>()
    val fixedMenuDataFood: MutableLiveData<GetFixedMenuResponse> = _fixedMenuDataFood

    private val _menuBymealId = MutableLiveData<ChangeMenuInfoListDto>()
    val menuBymealId: MutableLiveData<ChangeMenuInfoListDto> = _menuBymealId


    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time,
    ) {
        viewModelScope.launch {
            mealService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())
                .enqueue(object : Callback<BaseResponse<ArrayList<GetMealResponse>>> {
                    override fun onResponse(
                        call: Call<BaseResponse<ArrayList<GetMealResponse>>>,
                        response: Response<BaseResponse<ArrayList<GetMealResponse>>>,
                    ) {
                        val data = response.body()?.result

                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공" + response.body())

                            when (restaurantType) {
                                Restaurant.HAKSIK -> _todayMealDataHaksik.postValue(data!!)
                                Restaurant.DODAM -> _todayMealDataDodam.postValue(data!!)
                                Restaurant.DORMITORY -> _todayMealDataDormitory.postValue(data!!)

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

                    override fun onFailure(
                        call: Call<BaseResponse<ArrayList<GetMealResponse>>>,
                        t: Throwable,
                    ) {
                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
                    }
                })
        }
    }

    // Fixed Menu 데이터 로드도 유사한 방식으로 구현
    fun loadFixedMenu(restaurantType: Restaurant) {
        viewModelScope.launch {
            menuService.getFixMenu(restaurantType.toString())
                .enqueue(object : Callback<BaseResponse<GetFixedMenuResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<GetFixedMenuResponse>>,
                        response: Response<BaseResponse<GetFixedMenuResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공" + response.body())
                            val data = response.body()?.result!!
                            when (restaurantType) {
                                Restaurant.THE_KITCHEN -> _fixedMenuDataKitchen.postValue(data)
                                Restaurant.FOOD_COURT -> _fixedMenuDataFood.postValue(data)
                                Restaurant.SNACK_CORNER -> _fixedMenuDataSnack.postValue(data)

                                else -> {
                                    Log.d("post", "onResponse 실패. 잘못된 식당 입니다.")
                                }
                            }
                        } else {
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<GetFixedMenuResponse>>,
                        t: Throwable,
                    ) {
                        Log.d("post", "onFailure 에러: ${t.message}")
                    }
                })
        }
    }

    fun findMenuItemByMealId(mealId: Long) {
        viewModelScope.launch {
            mealService.getMenuInfoByMealId(mealId)
                .enqueue(object : Callback<BaseResponse<ChangeMenuInfoListDto>> {
                    override fun onResponse(
                        call: Call<BaseResponse<ChangeMenuInfoListDto>>,
                        response: Response<BaseResponse<ChangeMenuInfoListDto>>,
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.result!!
                            Log.d("post", "onResponse 성공" + response.body())

                            menuBymealId.postValue(data)

                        } else {
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<ChangeMenuInfoListDto>>,
                        t: Throwable,
                    ) {
                        Log.d("post", "onFailure 에러: ${t.message}")
                    }
                })
        }
    }
}