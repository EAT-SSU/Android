package com.eatssu.android.presentation.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.eatssu.android.R

enum class ToastType(@DrawableRes val iconId: Int, @DrawableRes val shapeId: Int) {
    INFO(R.drawable.ic_toast_info, R.drawable.shape_toast_info),
    SUCCESS(R.drawable.ic_toast_success, R.drawable.shape_toast_success),
    WARNING(R.drawable.ic_toast_warning, R.drawable.shape_toast_warning),
    ERROR(R.drawable.ic_toast_error, R.drawable.shape_toast_error)
}

enum class ToastLocation(val gravity: Int, val xOffset: Int, val yOffset: Int) {
    BOTTOM_NAVIGATION(Gravity.BOTTOM, 0, 200),
}

fun Context.showToast(
    message: String,
    type: ToastType,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    val toast = Toast(this)
    toast.setGravity(location.gravity, location.xOffset, location.yOffset)
    toast.duration = Toast.LENGTH_SHORT

    val inflater = LayoutInflater.from(this)
    val layout = inflater.inflate(R.layout.toast_layout, root)

    layout.findViewById<ImageView>(R.id.ic_toast).setImageResource(type.iconId)
    layout.findViewById<LinearLayout>(R.id.toast_layout).setBackgroundResource(type.shapeId)
    layout.findViewById<TextView>(R.id.toast_text).text = message

    toast.view = layout
    toast.show()
}

fun Fragment.showToast(
    message: String,
    type: ToastType,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showToast(message, type, location, root)
}

fun Context.showInfoToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.INFO, location, root)
}

fun Context.showInfoToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.INFO, location, root)
}

fun Fragment.showInfoToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showInfoToast(message, location, root)
}

fun Fragment.showInfoToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showInfoToast(messageId, location, root)
}

fun Context.showSuccessToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.SUCCESS, location, root)
}

fun Context.showSuccessToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.SUCCESS, location, root)
}

fun Fragment.showSuccessToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showSuccessToast(message, location, root)
}

fun Fragment.showSuccessToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showSuccessToast(messageId, location, root)
}

fun Context.showWarningToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.WARNING, location, root)
}

fun Context.showWarningToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.WARNING, location, root)
}

fun Fragment.showWarningToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showWarningToast(message, location, root)
}

fun Fragment.showWarningToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showWarningToast(messageId, location, root)
}

fun Context.showErrorToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.ERROR, location, root)
}

fun Context.showErrorToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(getString(messageId), ToastType.ERROR, location, root)
}

fun Fragment.showErrorToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showErrorToast(message, location, root)
}

fun Fragment.showErrorToast(
    @StringRes messageId: Int,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    requireContext().showErrorToast(messageId, location, root)
}