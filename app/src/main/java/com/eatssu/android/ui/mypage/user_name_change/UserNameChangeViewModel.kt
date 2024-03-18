package com.eatssu.android.ui.mypage.user_name_change

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserNameChangeViewModel(private val userService: UserService) : ViewModel() {

    private val _isEnableNickname = MutableLiveData<Boolean>()
    val isEnableNickname: LiveData<Boolean> get() = _isEnableNickname

    private val _isDone = MutableLiveData<Boolean>()
    val isDone: LiveData<Boolean> get() = _isDone

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun checkNickname(inputNickname: String) {
        userService.checkNickname(inputNickname).enqueue(object : Callback<BaseResponse<Boolean>> {
            override fun onResponse(
                call: Call<BaseResponse<Boolean>>,
                response: Response<BaseResponse<Boolean>>,
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.result == true) {
                        handleSuccessResponse("사용가능한 닉네임 입니다.")
                    } else {
                        handleErrorResponse("이미 사용 중인 닉네임 입니다.")
                    }
                } else {
                    handleErrorResponse("닉네임 중복 확인에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Boolean>>, t: Throwable) {
                handleErrorResponse("닉네임 중복 확인에 실패했습니다.")
            }
        })
    }

    fun changeNickname(inputNickname: String) {
        userService.changeNickname(ChangeNicknameRequest(inputNickname))
            .enqueue(object : Callback<BaseResponse<Void>> {
                override fun onResponse(
                    call: Call<BaseResponse<Void>>,
                    response: Response<BaseResponse<Void>>,
                ) {
                    if (response.isSuccessful) {
                        handleSuccessResponse("닉네임 설정에 성공했습니다.")
                    } else {
                        handleErrorResponse("닉네임 설정에 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Void>>, t: Throwable) {
                    handleErrorResponse("닉네임 설정에 실패했습니다.")
            }
        })
    }

    private fun handleSuccessResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isEnableNickname.value = true
            _isDone.value = true

        }
    }



    private fun handleErrorResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isEnableNickname.value = false
            _isDone.value = false
        }
    }
}
