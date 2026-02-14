package com.eatssu.android.test

import app.cash.turbine.ReceiveTurbine
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import io.kotest.matchers.shouldBe

fun UiText?.asStringResIdOrNull(): Int? = (this as? UiText.StringResource)?.resId

suspend fun ReceiveTurbine<UiEvent>.awaitToastEvent(): UiEvent.ShowToast {
    return awaitItem() as UiEvent.ShowToast
}

fun UiEvent.ShowToast.assertToast(resId: Int, type: com.eatssu.common.enums.ToastType) {
    message.asStringResIdOrNull() shouldBe resId
    this.type shouldBe type
}
