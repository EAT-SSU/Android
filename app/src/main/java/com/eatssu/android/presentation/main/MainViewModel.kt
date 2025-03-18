package com.eatssu.android.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.auth.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

//    init {
//        checkNameNull()
//    } 얘 떄문에 두번씩 처리됨.

    fun checkNameNull() {
        viewModelScope.launch {
            getUserInfoUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        error = true,
                        toastMessage = context.getString(R.string.not_found)
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())
                result.result?.apply {
                    if (this.nickname.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                isNicknameNull = true,
                                toastMessage = context.getString(R.string.set_nickname)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isNicknameNull = false,
                                toastMessage = String.format(
                                    context.getString(R.string.hello_user),
                                    this.nickname
                                )
                            )
                        }
                    }
                }
            }
        }

    }
}


data class MainState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var isNicknameNull: Boolean = false,
)