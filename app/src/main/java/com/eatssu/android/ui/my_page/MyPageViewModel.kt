package com.eatssu.android.ui.my_page

import androidx.lifecycle.ViewModel
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MyInfoResponse
import com.eatssu.android.data.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageViewModel(private val userService: UserService) : ViewModel() {

    private val _uiState: MutableStateFlow<MyPageState> = MutableStateFlow(MyPageState())
    val uiState: StateFlow<MyPageState> = _uiState.asStateFlow()

    fun checkMyInfo() {
        userService.getMyInfo().enqueue(object : Callback<BaseResponse<MyInfoResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<MyInfoResponse>>,
                response: Response<BaseResponse<MyInfoResponse>>,
            ) {
                if (response.isSuccessful) {

                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = false,
                            nickname = response.body()?.result?.nickname.toString(),
                            platform = response.body()?.result!!.provider
                        )
                    }

                    if (response.body()?.result?.nickname.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                isNicknameNull = true,
                                toastMessage = "닉네임을 설정해주세요."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                isNicknameNull = false,
                                toastMessage = "${response.body()?.result?.nickname} 님 환영합니다."
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = true,
                            toastMessage = "정보를 불러 올 수 없습니다."
                        )
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<MyInfoResponse>>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        toastMessage = "정보를 불러 올 수 없습니다."
                    )
                }
            }
        })
    }
}


data class MyPageState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var nickname: String = "",
    var platform: String = "",

    var isNicknameNull: Boolean = false,

    )