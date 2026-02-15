package com.eatssu.android.test

import app.cash.turbine.ReceiveTurbine
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import io.kotest.matchers.shouldBe

fun UiText?.asStringResIdOrNull(): Int? = (this as? UiText.StringResource)?.resId

suspend fun ReceiveTurbine<UiEvent>.awaitToastEvent(): UiEvent.ShowToast {
    return awaitItem() as UiEvent.ShowToast
}

fun UiEvent.ShowToast.assertToast(resId: Int, type: ToastType) {
    message.asStringResIdOrNull() shouldBe resId
    this.type shouldBe type
}

suspend fun ReceiveTurbine<UiEvent>.expectToast(resId: Int, type: ToastType): UiEvent.ShowToast {
    return awaitToastEvent().also { it.assertToast(resId, type) }
}

suspend fun ReceiveTurbine<UiEvent>.expectNavigateBack() {
    awaitItem() shouldBe UiEvent.NavigateBack
}

inline fun <reified T> UiState<*>.successDataAs(): T {
    val success = this as? UiState.Success<*>
        ?: error("Expected UiState.Success but was $this")
    return success.data as? T
        ?: error("Expected success data type ${T::class}, but was ${success.data?.let { it::class }}")
}
