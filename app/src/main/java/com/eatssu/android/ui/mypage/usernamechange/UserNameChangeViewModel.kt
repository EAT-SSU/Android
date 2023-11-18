package com.eatssu.android.ui.mypage.usernamechange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserNameChangeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isEnableNickname = MutableLiveData<Boolean>()
    val isEnableNickname: LiveData<Boolean> get() = _isEnableNickname

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun checkNickname(inputNickname: String) {
        userRepository.nicknameCheck(inputNickname, object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (response.body() == "true") {
                        handleSuccessResponse("사용가능한 닉네임 입니다.")
                    } else {
                        handleErrorResponse("이미 사용 중인 닉네임 입니다.")
                    }
                } else {
                    handleErrorResponse("닉네임 중복 확인에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                handleErrorResponse("닉네임 중복 확인에 실패했습니다.")
            }
        })
    }

    fun changeNickname(inputNickname: String) {
        userRepository.nicknameChange(inputNickname, object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    handleSuccessResponse("닉네임 변경에 성공했습니다.")
                } else {
                    handleErrorResponse("닉네임 변경에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleErrorResponse("닉네임 변경에 실패했습니다.")
            }
        })
    }

    private fun handleSuccessResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isEnableNickname.value = true

        }
    }



    private fun handleErrorResponse(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _toastMessage.value = message
            _isEnableNickname.value = false
        }
    }
}
