package com.eatssu.android.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import okhttp3.Call
import okhttp3.Callback
import retrofit2.Response

// MenuRepository.kt
class MenuRepository(private val menuService: MenuService) {
    fun getTodayMeal(restaurantType: Restaurant, time: String): LiveData<GetTodayMealResponseDto> {
        val resultLiveData = MutableLiveData<GetTodayMealResponseDto>()

        // Call the API using menuService.getTodayMeal and update resultLiveData

        return resultLiveData
    }


    fun getFixedMenu(restaurantType: Restaurant): MutableLiveData<List<GetFixedMenuResponseDto.FixMenuInfoList>?> {
        val resultLiveData = MutableLiveData<List<GetFixedMenuResponseDto.FixMenuInfoList>?>()

//        menuService.getFixMenu(restaurantType.toString()).enqueue(object : Callback<GetFixedMenuResponseDto> {
//            override fun onResponse(call: Call<GetFixedMenuResponseDto>, response: Response<GetFixedMenuResponseDto>) {
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    val fixMenuInfoList = body?.fixMenuInfoList
//                    resultLiveData.value = fixMenuInfoList
//                } else {
//                    // Handle error case
//                    resultLiveData.value = emptyList()
//                }
//            }
//
//            override fun onFailure(call: Call<GetFixedMenuResponseDto>, t: Throwable) {
//                // Handle failure case
//                resultLiveData.value = emptyList()
//            }
//        })

        return resultLiveData
    }

//    fun getFixedMenu(restaurantType: RestaurantType): LiveData<GetFixedMenuResponse> {
//        val resultLiveData = MutableLiveData<GetFixedMenuResponse>()
//
//        // Call the API using menuService.getFixedMenu and update resultLiveData
//
//        menuService.getFixMenu(restaurantType.toString())
//            .enqueue(object : Callback<GetFixedMenuResponse> {
//                override fun onResponse(
//                    call: Call<GetFixedMenuResponse>,
//                    response: Response<GetFixedMenuResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val body = response.body()
//                        body?.let {
//                            setAdapter(it.fixMenuInfoList, recyclerView, restaurantType)
//                        }
//                        Log.d("post", "onResponse 성공" + response.body())
//
//                    } else {
//                        Log.d("post", "onResponse 실패")
//                    }
//                }
//
//                override fun onFailure(call: Call<GetFixedMenuResponse>, t: Throwable) {
//                    Log.d("post", "onFailure 에러: ${t.message}")
//                }
//            })
//        return resultLiveData
//    }


}
