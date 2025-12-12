package com.eatssu.android.presentation.util

import androidx.annotation.DrawableRes
import com.eatssu.android.R
import com.eatssu.common.enums.ToastType

/**
 * common 모듈의 ToastType은 순수한 enum이므로 Android 리소스에 접근 불가
 * 따라서 ToastExtensions.kt에서 리소스 매핑 제공
 */

@get:DrawableRes
val ToastType.iconId: Int
    get() = when (this) {
        ToastType.INFO -> R.drawable.ic_toast_info
        ToastType.SUCCESS -> R.drawable.ic_toast_success
        ToastType.WARNING -> R.drawable.ic_toast_warning
        ToastType.ERROR -> R.drawable.ic_toast_error
    }

@get:DrawableRes
val ToastType.shapeId: Int
    get() = when (this) {
        ToastType.INFO -> R.drawable.shape_toast_info
        ToastType.SUCCESS -> R.drawable.shape_toast_success
        ToastType.WARNING -> R.drawable.shape_toast_warning
        ToastType.ERROR -> R.drawable.shape_toast_error
    }
