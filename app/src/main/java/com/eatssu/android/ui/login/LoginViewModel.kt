package com.eatssu.android.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.model.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.TokenResponseDto
import com.eatssu.android.data.service.OauthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val oauthService: OauthService): ViewModel() {

    private val _getLoginDataSuccessResponse = MutableLiveData<TokenResponseDto?>()
    val getHomeLoginSuccessResponse: LiveData<TokenResponseDto?> = _getLoginDataSuccessResponse

    private val _getLoginDataErrorResponse = MutableLiveData<String>()
    val getHomeLoginErrorResponse: LiveData<String> = _getLoginDataErrorResponse

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

//    fun getLogin(email: String, providerID: String) {
//        viewModelScope.launch {
//            kotlin.runCatching {
//                oauthService.loginWithKakao(LoginWithKakaoRequestDto(email, providerID))
//            }.fold(onSuccess = { successResponse ->
//                _getLoginDataSuccessResponse.value = successResponse.result
//            }, onFailure = { errorResponse ->
//                _getLoginDataErrorResponse.value = errorResponse.message
//            })
//        }
//    }

    fun getLogin(email: String, providerID: String) {
        oauthService.loginWithKakao(LoginWithKakaoRequestDto(email,providerID)).enqueue(object : Callback<BaseResponse<TokenResponseDto>> {
            override fun onResponse(call: Call<BaseResponse<TokenResponseDto>>, response: Response<BaseResponse<TokenResponseDto>>) {
                if (response.isSuccessful) {
                    val data = response.body()!!
                    if (data.isSuccess) {
                        _getLoginDataSuccessResponse.value = data.result
                    } else {
                        _getLoginDataErrorResponse.value = data.message
                        handleErrorResponse("가입 안됨.")
                    }
                } else {
                    handleErrorResponse("가입 안됨.")
                }
            }

            override fun onFailure(call: Call<BaseResponse<TokenResponseDto>>, t: Throwable) {
                handleErrorResponse("네트워크 오류입니다.")
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