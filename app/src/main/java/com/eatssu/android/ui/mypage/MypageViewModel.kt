package com.eatssu.android.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.repository.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@HiltViewModel
class MypageViewModel(private val myPageRepository: MyPageRepository) : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _isNull = MutableLiveData<Boolean>()
    val isNull: LiveData<Boolean> get() = _isNull

    fun checkMyInfo() {
        myPageRepository.myInfoCheck(object : Callback<GetMyInfoResponseDto> {
            override fun onResponse(call: Call<GetMyInfoResponseDto>, response: Response<GetMyInfoResponseDto>) {
                if (response.isSuccessful) {
                    _nickname.postValue(response.body()?.nickname)

                    if (response.body()?.nickname.isNullOrBlank()) {
                        handleErrorResponse("환영합니다.") //null이면 isNull에 true를 넣음
                        _isNull.postValue(true)
                    } else {
                        handleSuccessResponse("${response.body()?.nickname} 님 환영합니다.")
                        _isNull.postValue(false)

                    }
                } else {
                    handleErrorResponse("정보를 불러 올 수 없습니다.")
                }
            }

            override fun onFailure(call: Call<GetMyInfoResponseDto>, t: Throwable) {
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
