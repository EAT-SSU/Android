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
    location: ToastLocation,
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

fun Context.showInfoToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.INFO, location, root)
}

fun Context.showSuccessToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.SUCCESS, location, root)
}

fun Context.showWarningToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.WARNING, location, root)
}

fun Context.showErrorToast(
    message: String,
    location: ToastLocation = ToastLocation.BOTTOM_NAVIGATION,
    root: ViewGroup? = null,
) {
    showToast(message, ToastType.ERROR, location, root)
}

