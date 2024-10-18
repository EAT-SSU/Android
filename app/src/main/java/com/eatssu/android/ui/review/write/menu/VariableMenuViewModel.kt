package com.eatssu.android.ui.review.write.menu


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import com.eatssu.android.data.dto.response.asMenuOfMeal
import com.eatssu.android.data.model.MenuMini
import com.eatssu.android.data.service.MealService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VariableMenuViewModel(
    private val mealService: MealService,
) :
    ViewModel() {

    private val _uiState: MutableStateFlow<MenuState> = MutableStateFlow(MenuState())
    val uiState: StateFlow<MenuState> = _uiState.asStateFlow()


    fun findMenuItemByMealId(mealId: Long) {
        viewModelScope.launch {
            mealService.getMenuInfoByMealId(mealId)
                .enqueue(object : Callback<BaseResponse<MenuOfMealResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<MenuOfMealResponse>>,
                        response: Response<BaseResponse<MenuOfMealResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.result
                            Log.d("post", "onResponse 성공" + response.body())
                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    error = false,
                                    menuOfMeal = response.body()?.result?.asMenuOfMeal()
                                )
                            }
                        } else {
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<MenuOfMealResponse>>,
                        t: Throwable,
                    ) {
                        Log.d("post", "onFailure 에러: ${t.message}")
                    }
                })
        }
    }
}

data class MenuState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var menuOfMeal: List<MenuMini>? = null,
)