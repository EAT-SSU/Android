package com.eatssu.android.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eatssu.android.databinding.ToastLayoutBinding
import com.eatssu.android.presentation.map.findActivityOrNull
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

@SuppressLint("RestrictedApi")
fun Context.showToast(
    message: String,
    type: ToastType,
) {
    val viewGroup =
        findActivityOrNull()?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)

    if (viewGroup == null) {
        Timber.w("ViewGroup is null - cannot show toast. Context type: ${this::class.simpleName}, Activity: ${findActivityOrNull()?.javaClass?.simpleName}")
        return
    }

    val binding = ToastLayoutBinding.inflate(LayoutInflater.from(this), viewGroup, false).apply {
        ivToast.setImageResource(type.iconId)
        toastLayout.setBackgroundResource(type.shapeId)
        tvToastText.text = message
    }

    val snackbar = Snackbar.make(viewGroup, "", 5000)
    val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    with(snackbarLayout) {
        removeAllViews()
        setPadding(0, 0, 0, 0)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))

        // Snackbar를 화면 가로로 꽉 채우고 정해진 위치에 띄워지게 설정
        layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
            width = FrameLayout.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM

            val horizontalMargin = (16 * resources.displayMetrics.density).toInt()
            val bottomMargin = (84 * resources.displayMetrics.density).toInt()
            setMargins(horizontalMargin, 0, horizontalMargin, bottomMargin)
        }

        addView(binding.root)
    }

    snackbar.show()
}

fun Context.showToast(@StringRes messageId: Int, type: ToastType) =
    showToast(getString(messageId), type)

fun Context.showToast(uiText: UiText, type: ToastType) =
    showToast(uiText.asString(this), type)

fun Context.showToast(event: UiEvent.ShowToast) =
    showToast(event.message.asString(this), event.type)

fun Fragment.showToast(event: UiEvent.ShowToast) =
    requireContext().showToast(event.message.asString(requireContext()), event.type)

fun Fragment.showToast(message: String, type: ToastType) =
    requireContext().showToast(message, type)

fun Context.showInfoToast(message: String) =
    showToast(message, ToastType.INFO)

fun Context.showInfoToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.INFO)

fun Fragment.showInfoToast(message: String) =
    requireContext().showInfoToast(message)

fun Fragment.showInfoToast(@StringRes messageId: Int) =
    requireContext().showInfoToast(messageId)

fun Context.showSuccessToast(message: String) =
    showToast(message, ToastType.SUCCESS)

fun Context.showSuccessToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.SUCCESS)

fun Fragment.showSuccessToast(message: String) =
    requireContext().showSuccessToast(message)

fun Fragment.showSuccessToast(@StringRes messageId: Int) =
    requireContext().showSuccessToast(messageId)

fun Context.showWarningToast(message: String) =
    showToast(message, ToastType.WARNING)

fun Context.showWarningToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.WARNING)

fun Fragment.showWarningToast(message: String) =
    requireContext().showWarningToast(message)

fun Fragment.showWarningToast(@StringRes messageId: Int) =
    requireContext().showWarningToast(messageId)

fun Context.showErrorToast(message: String) =
    showToast(message, ToastType.ERROR)

fun Context.showErrorToast(@StringRes messageId: Int) =
    showToast(getString(messageId), ToastType.ERROR)

fun Fragment.showErrorToast(message: String) =
    requireContext().showErrorToast(message)

fun Fragment.showErrorToast(@StringRes messageId: Int) =
    requireContext().showErrorToast(messageId)