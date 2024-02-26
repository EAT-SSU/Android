package com.eatssu.android.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetMyInfoResponse
import com.eatssu.android.data.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageViewModel(private val userService: UserService) : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _nickname = MutableLiveData<String?>()
    val nickname: LiveData<String?> get() = _nickname

    private val _isNull = MutableLiveData<Boolean>()
    val isNull: LiveData<Boolean> get() = _isNull

    fun checkMyInfo() {
        userService.getMyInfo().enqueue(object : Callback<BaseResponse<GetMyInfoResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<GetMyInfoResponse>>,
                response: Response<BaseResponse<GetMyInfoResponse>>,
            ) {
                if (response.isSuccessful) {
                    _nickname.postValue(response.body()?.result?.nickname)

                    if (response.body()?.result?.nickname.isNullOrBlank()) {
                        handleErrorResponse("환영합니다.") //null이면 isNull에 true를 넣음
                        _isNull.postValue(true)
                    } else {
                        handleSuccessResponse("${response.body()?.result?.nickname} 님 환영합니다.")
                        _isNull.postValue(false)

                    }
                } else {
                    handleErrorResponse("정보를 불러 올 수 없습니다.")
                }
            }

            override fun onFailure(call: Call<BaseResponse<GetMyInfoResponse>>, t: Throwable) {
                handleErrorResponse("정보를 불러 올 수 없습니다.")
            }
        })
    }

    private fun handleSuccessResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message

        }
    }



    private fun handleErrorResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
        }
    }
}
