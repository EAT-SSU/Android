package com.eatssu.android.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.model.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.model.response.TokenResponseDto
import com.eatssu.android.data.service.OauthService
import kotlinx.coroutines.launch

class LoginViewModel(private val oauthService: OauthService): ViewModel() {

    private val _getLoginDataSuccessResponse = MutableLiveData<TokenResponseDto?>()
    val getHomeLoginSuccessResponse: LiveData<TokenResponseDto?> = _getLoginDataSuccessResponse

    private val _getLoginDataErrorResponse = MutableLiveData<String>()
    val getHomeLoginErrorResponse: LiveData<String> = _getLoginDataErrorResponse

    fun getLogin(email: String, providerID: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                oauthService.loginWithKakao(LoginWithKakaoRequestDto(email, providerID))
            }.fold(onSuccess = { successResponse ->
                _getLoginDataSuccessResponse.value = successResponse.result
            }, onFailure = { errorResponse ->
                _getLoginDataErrorResponse.value = errorResponse.message
            })
        }
    }

}