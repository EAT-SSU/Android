package com.eatssu.common

sealed interface UiState<out T> {
    object Init : UiState<Nothing>

    object Loading : UiState<Nothing>

    data class Success<out T>(
        val data: T,
    ) : UiState<T>

    object Error : UiState<Nothing>
}