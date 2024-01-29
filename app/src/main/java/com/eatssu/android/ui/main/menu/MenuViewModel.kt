package com.eatssu.android.ui.main.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.ChangeMenuInfoListDto
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuViewModel(private val menuService: MenuService) : ViewModel() {

//    private val _getHomeDataSuccessResponse = MutableLiveData<ResponseHomeDto?>()
//    val getHomeDataSuccessResponse: LiveData<ResponseHomeDto?> = _getHomeDataSuccessResponse
//
//    private val _getHomeDataErrorResponse = MutableLiveData<String>()
//    val getHomeDataErrorResponse: LiveData<String> = _getHomeDataErrorResponse


    private val _getTodayMealSuccessResponse = MutableLiveData<GetTodayMealResponseDto?>()
    val getTodayMealSuccessResponse: LiveData<GetTodayMealResponseDto?> =
        _getTodayMealSuccessResponse

    private val _getTodayMealErrorResponse = MutableLiveData<String>()
    val getTodayMealErrorResponse: LiveData<String> = _getTodayMealErrorResponse


    private val _todayMealDataDodam = MutableLiveData<GetTodayMealResponseDto?>()
    val todayMealDataDodam: MutableLiveData<GetTodayMealResponseDto?> = _todayMealDataDodam

    private val _todayMealDataHaksik = MutableLiveData<GetTodayMealResponseDto?>()
    val todayMealDataHaksik: MutableLiveData<GetTodayMealResponseDto?> = _todayMealDataHaksik

    private val _todayMealDataDormitory = MutableLiveData<GetTodayMealResponseDto?>()
    val todayMealDataDormitory: MutableLiveData<GetTodayMealResponseDto?> = _todayMealDataDormitory

    private val _fixedMenuDataKitchen = MutableLiveData<GetFixedMenuResponseDto?>()
    val fixedMenuDataKitchen: MutableLiveData<GetFixedMenuResponseDto?> = _fixedMenuDataKitchen

    private val _fixedMenuDataSnack = MutableLiveData<GetFixedMenuResponseDto?>()
    val fixedMenuDataSnack: MutableLiveData<GetFixedMenuResponseDto?> = _fixedMenuDataSnack

    private val _fixedMenuDataFood = MutableLiveData<GetFixedMenuResponseDto?>()
    val fixedMenuDataFood: MutableLiveData<GetFixedMenuResponseDto?> = _fixedMenuDataFood

    private val _menuBymealId = MutableLiveData<ChangeMenuInfoListDto>()
    val menuBymealId: MutableLiveData<ChangeMenuInfoListDto> = _menuBymealId


    fun loadTodayMeal(
        menuDate: String,
        restaurantType: Restaurant,
        time: Time
    ) {
        viewModelScope.launch {
            kotlin.runCatching {
                menuService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())
            }.fold(onSuccess = { successResponse ->
                _getTodayMealSuccessResponse.value = successResponse.result
                val data = successResponse.result


                when (restaurantType) {
                    Restaurant.HAKSIK -> _todayMealDataHaksik.postValue(data)
                    Restaurant.DODAM -> _todayMealDataDodam.postValue(data)
                    Restaurant.DORMITORY -> _todayMealDataDormitory.postValue(data)
                    else -> {
                        Log.d("post", "onResponse 실패. 잘못된 식당입니다.")
                    }
                }
            }, onFailure = { errorResponse ->
                _getTodayMealErrorResponse.value = errorResponse.message

                Log.d("post", "onResponse 실패 투데이밀" + errorResponse.message)
            })

//            menuService.getTodayMeal(menuDate, restaurantType.toString(), time.toString())
//                .enqueue(object : Callback<BaseResponse<GetTodayMealResponseDto>> {
//                    override fun onResponse(
//                        call: Call<BaseResponse<GetTodayMealResponseDto>>,
//                        response: Response<BaseResponse<GetTodayMealResponseDto>>
//                    ) {
//                        if (response.isSuccessful) {
//                            Log.d("post", "onResponse 성공" + response.body())
//
//                            val data = response.body()!!.result
//
//                            when (restaurantType) {
//                                Restaurant.HAKSIK -> _todayMealDataHaksik.postValue(data)
//                                Restaurant.DODAM -> _todayMealDataDodam.postValue(data)
//                                Restaurant.DORMITORY -> _todayMealDataDormitory.postValue(data)
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
//                    override fun onFailure(call: Call<BaseResponse<GetTodayMealResponseDto>>, t: Throwable) {
//                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
//                    }
//                })
//        }
        }
    }

        // Fixed Menu 데이터 로드도 유사한 방식으로 구현
        fun loadFixedMenu(restaurantType: Restaurant) {
            viewModelScope.launch {
                menuService.getFixMenu(restaurantType.toString())
                    .enqueue(object : Callback<BaseResponse<GetFixedMenuResponseDto>> {
                        override fun onResponse(
                            call: Call<BaseResponse<GetFixedMenuResponseDto>>,
                            response: Response<BaseResponse<GetFixedMenuResponseDto>>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("post", "onResponse 성공" + response.body())
                                val data = response.body()!!.result

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
                            call: Call<BaseResponse<GetFixedMenuResponseDto>>,
                            t: Throwable
                        ) {
                            Log.d("post", "onFailure 에러: ${t.message}")
                        }
                    })
            }
        }

        fun findMenuItemByMealId(mealId: Long) {
            viewModelScope.launch {
                menuService.getMenuByMealId(mealId)
                    .enqueue(object : Callback<BaseResponse<ChangeMenuInfoListDto>> {
                        override fun onResponse(
                            call: Call<BaseResponse<ChangeMenuInfoListDto>>,
                            response: Response<BaseResponse<ChangeMenuInfoListDto>>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("post", "onResponse 성공" + response.body())

                                menuBymealId.postValue(response.body()!!.result!!)

                            } else {
                                Log.d("post", "onResponse 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<BaseResponse<ChangeMenuInfoListDto>>,
                            t: Throwable
                        ) {
                            Log.d("post", "onFailure 에러: ${t.message}")
                        }
                    })
            }
        }
    }
