package com.eatssu.android.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.repository.ReviewListRepository
import com.eatssu.android.view.review.ItemData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReviewListViewModel(private val repository: ReviewListRepository) : ViewModel() {

    private val _type = MutableLiveData<MenuType>()
    val type: LiveData<MenuType> = _type

//    private val selectedItemId = MutableLiveData<Long>()
    private val selectedItemId = MutableLiveData<Long>()
//    val selectedItemId: LiveData<MenuType> = _selectedItemId

//    private val _mealId = MutableLiveData<Long>()
//    val mealId: LiveData<Long> = _mealId
//
//    private val _menuId = MutableLiveData<Long>()
//    val menuId: LiveData<Long> = _menuId

//    private val selectedItemId = MutableLiveData<Long>()
    private val selectedMenuType = MutableLiveData<MenuType>()

    private val _reviewList = MutableLiveData<GetReviewListResponse>()
    val reviewList: LiveData<GetReviewListResponse> = _reviewList

    private val _reviewInfo = MutableLiveData<GetReviewInfoResponseDto>()
    val reviewInfo: LiveData<GetReviewInfoResponseDto> = _reviewInfo



    //최후의 방법
    val selectedItem = MutableLiveData<ItemData>()

    fun setSelectedItem(itemData: ItemData) {
        selectedItem.value = itemData
    }


    fun setSelectedItemId(itemId: Long) {
        selectedItemId.value = itemId
    }

    fun getSelectedItemId(): LiveData<Long> {
        return selectedItemId
    }

    fun setSelectedMenuType(menuType: MenuType) {
        selectedMenuType.value = menuType
    }

    fun getSelectedMenuType(): LiveData<MenuType> {
        return selectedMenuType
    }

    fun getLiveDataMenuIdAndMenuType(): LiveData<Pair<MenuType,Long>> {
        return MediatorLiveData<Pair<MenuType,Long>>().apply {
            addSource(selectedMenuType) { value1 ->
                val value2 = selectedItemId.value ?: 0
                value = Pair(value1, value2)
            }
            addSource(selectedItemId) { value2 ->
                val value1 = selectedMenuType.value ?: MenuType.FIX
                value = Pair(value1, value2)
            }
        }
    }



    fun loadReviewList(
        menuType: MenuType,
        mealId: Long?,
        menuId: Long?,
//        lastReviewId: Long?,
//        page: Int?,
//        size: Int?,
    ) {
        viewModelScope.launch {

            repository.getReviewList(menuType.toString(), mealId, menuId)
                .enqueue(object : Callback<GetReviewListResponse> {
                    override fun onResponse(
                        call: Call<GetReviewListResponse>, response: Response<GetReviewListResponse>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString());
                            _reviewList.postValue(response.body())

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<GetReviewListResponse>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString());
                    }
                })
        }
    }

    fun loadReviewInfo(
        menuType: MenuType,
        mealId: Long?,
        menuId: Long?,
    ) {
        viewModelScope.launch {

            repository.getReviewInfo(menuType.toString(), mealId, menuId)
                .enqueue(object : Callback<GetReviewInfoResponseDto> {
                    override fun onResponse(
                        call: Call<GetReviewInfoResponseDto>,
                        response: Response<GetReviewInfoResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString());
                            _reviewInfo.postValue(response.body())

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<GetReviewInfoResponseDto>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString());
                    }
                })
        }
    }
}