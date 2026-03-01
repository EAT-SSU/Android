package com.eatssu.android.presentation.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType

fun Context.showToast(
    message: String,
    type: ToastType,
) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(
    @StringRes messageId: Int,
    type: ToastType,
) = showToast(getString(messageId), type)

fun Context.showToast(
    uiText: UiText,
    type: ToastType,
) = showToast(uiText.asString(this), type)

fun Context.showToast(event: UiEvent.ShowToast) =
    showToast(event.message.asString(this), event.type)

fun Context.showInfoToast(message: String) =
    showToast(message, ToastType.INFO)

fun Context.showInfoToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.INFO)

fun Context.showSuccessToast(message: String) =
    showToast(message, ToastType.SUCCESS)

fun Context.showSuccessToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.SUCCESS)

fun Context.showWarningToast(message: String) =
    showToast(message, ToastType.WARNING)

fun Context.showWarningToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.WARNING)

fun Context.showErrorToast(message: String) =
    showToast(message, ToastType.ERROR)

fun Context.showErrorToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.ERROR)
