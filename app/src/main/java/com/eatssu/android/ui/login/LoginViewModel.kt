package com.eatssu.android.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.App
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.data.MySharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@HiltViewModel
class LoginViewModel(private val oauthService: OauthService) : ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun getLogin(email: String, providerID: String) {
        viewModelScope.launch {
            oauthService.loginWithKakao(LoginWithKakaoRequest(email, providerID))
                .enqueue(object : Callback<BaseResponse<TokenResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<TokenResponse>>,
                        response: Response<BaseResponse<TokenResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {

                                _state.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        toastMessage = "$email 계정으로 로그인에 성공하였습니다.",
                                        tokens = response.body()?.result
                                    )
                                }


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
                                _state.update {
                                    it.copy(
                                        error = true,
                                        toastMessage = response.body()?.message.toString()
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<TokenResponse>>,
                        t: Throwable,
                    ) {
                        Log.d("LoginViewModel", "onFailure 에러: " + t.message.toString())
                        _state.update {
                            it.copy(
                                error = true,
                                toastMessage = t.message.toString()
                            )
                        }
                    }
                })
        }
    }

}

data class LoginState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
    var tokens: TokenResponse? = null,
)