package com.eatssu.android.presentation

sealed interface UiState<out T> {
    object Init : UiState<Nothing>

    object Loading : UiState<Nothing>

    data class Success<out T>(
        val data: T? = null,
    ) : UiState<T>

    object Error : UiState<Nothing>
}