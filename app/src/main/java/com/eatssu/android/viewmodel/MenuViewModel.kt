package com.eatssu.android.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
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



    fun loadTodayMeal(menuDate: String, restaurantType: Restaurant, time: Time, recyclerView: RecyclerView) {
        viewModelScope.launch {
            val response = repository.getTodayMeal(menuDate, restaurantType, time)
                .enqueue(object : Callback<GetTodayMealResponseDto> {
                    override fun onResponse(
                        call: Call<GetTodayMealResponseDto>,
                        response: Response<GetTodayMealResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
//                            body?.let {
//                                setAdapterTodayMeal(it, restaurantType,recyclerView)
//                            }

                            Log.d("post", "onResponse 성공 투데이밀" + response.body())

                        } else {
                            Log.d("post", "onResponse 실패 투데이밀" + response.code()+response.message())
                        }
                    }

                    override fun onFailure(call: Call<GetTodayMealResponseDto>, t: Throwable) {
                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
                    }
                })
        }

    }

    // Fixed Menu 데이터 로드도 유사한 방식으로 구현
     fun loadFixedMenu(restaurantType: Restaurant): Call<GetFixedMenuResponseDto> {
        return repository.getFixedMenu(restaurantType)
    }
}
