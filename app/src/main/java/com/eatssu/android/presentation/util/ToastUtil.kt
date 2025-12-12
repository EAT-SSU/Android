package com.eatssu.android.presentation.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.eatssu.android.R
import com.eatssu.common.UiEvent
import com.eatssu.common.enums.ToastType

fun Context.showToast(
    message: String,
    type: ToastType,
    root: ViewGroup? = null,
) {
    val toast = Toast(this)
    toast.setGravity(Gravity.BOTTOM, 0, 200)
    toast.duration = Toast.LENGTH_SHORT

    val inflater = LayoutInflater.from(this)
    val layout = inflater.inflate(R.layout.toast_layout, root)

    layout.findViewById<ImageView>(R.id.ic_toast).setImageResource(type.iconId)
    layout.findViewById<LinearLayout>(R.id.toast_layout).setBackgroundResource(type.shapeId)
    layout.findViewById<TextView>(R.id.toast_text).text = message

    toast.view = layout
    toast.show()
}

fun Context.showToast(event: UiEvent.ShowToast) =
    showToast(event.message, event.type)

fun Fragment.showToast(event: UiEvent.ShowToast) =
    showToast(event.message, event.type)

fun Fragment.showToast(
    message: String,
    type: ToastType,
    root: ViewGroup? = null,
) {
    requireContext().showToast(message, type, root)
}

fun Context.showInfoToast(
    message: String,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.INFO, root)
}

fun Context.showInfoToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.INFO, root)
}

fun Fragment.showInfoToast(
    message: String,
    root: ViewGroup? = null,
) {
    requireContext().showInfoToast(message, root)
}

fun Fragment.showInfoToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    requireContext().showInfoToast(messageId, root)
}

fun Context.showSuccessToast(
    message: String,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.SUCCESS, root)
}

fun Context.showSuccessToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.SUCCESS, root)
}

fun Fragment.showSuccessToast(
    message: String,
    root: ViewGroup? = null,
) {
    requireContext().showSuccessToast(message, root)
}

fun Fragment.showSuccessToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    requireContext().showSuccessToast(messageId, root)
}

fun Context.showWarningToast(
    message: String,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.WARNING, root)
}

fun Context.showWarningToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.WARNING, root)
}

fun Fragment.showWarningToast(
    message: String,
    root: ViewGroup? = null,
) {
    requireContext().showWarningToast(message, root)
}

fun Fragment.showWarningToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    requireContext().showWarningToast(messageId, root)
}

fun Context.showErrorToast(
    message: String,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.ERROR, root)
}

fun Context.showErrorToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.ERROR, root)
}

fun Fragment.showErrorToast(
    message: String,
    root: ViewGroup? = null,
) {
    requireContext().showErrorToast(message, root)
}

fun Fragment.showErrorToast(
    @StringRes messageId: Int,
    root: ViewGroup? = null,
) {
    requireContext().showErrorToast(messageId, root)
}