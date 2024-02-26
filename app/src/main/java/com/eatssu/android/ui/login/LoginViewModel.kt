package com.eatssu.android.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.App
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.dto.response.TokenResponseDto
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.util.MySharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val oauthService: OauthService) : ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    suspend fun getLogin(email: String, providerID: String) {
        viewModelScope.launch {
            oauthService.loginWithKakao(LoginWithKakaoRequestDto(email, providerID))
                .enqueue(object : Callback<BaseResponse<TokenResponseDto>> {
                    override fun onResponse(
                        call: Call<BaseResponse<TokenResponseDto>>,
                        response: Response<BaseResponse<TokenResponseDto>>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                Log.d(
                                    "LoginViewModel",
                                    "onResponse 성공: " + response.body().toString()
                                )
                                _state.value.toastMessage = "$email 계정으로 로그인에 성공하였습니다."

                                /*자동 로그인*/
                                MySharedPreferences.setUserEmail(App.appContext, email)
                                MySharedPreferences.setUserPlatform(App.appContext, "KAKAO")

                                /*토큰 저장*/
                                response.body()!!.result?.apply {
                                    App.token_prefs.accessToken = accessToken
                                    App.token_prefs.refreshToken = refreshToken//헤더에 붙일 토큰 저장
                                }


                            } else {
                                Log.d(
                                    "LoginViewModel",
                                    "onResponse 오류: " + response.body().toString()
                                )
                                _state.value.toastMessage = response.body()?.message.toString()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<TokenResponseDto>>,
                        t: Throwable,
                    ) {
                        Log.d("LoginViewModel", "onFailure 에러: " + t.message.toString())
                        _state.value.toastMessage = t.message.toString()
                    }
                })
        }
    }
}

data class LoginState(
    var toastMessage: String = "",
    val loading: Boolean = true,
    val error: Boolean = false,
    val tokens: TokenResponseDto? = null,
)