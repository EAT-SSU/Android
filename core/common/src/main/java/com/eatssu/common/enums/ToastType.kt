package com.eatssu.common.enums

import androidx.annotation.DrawableRes
import com.eatssu.common.R

/**
 * Toast 타입을 정의하는 Enum 클래스
 */
enum class ToastType(
    @field:DrawableRes val iconId: Int,
    @field:DrawableRes val shapeId: Int
) {
    INFO(R.drawable.ic_toast_info, R.drawable.shape_toast_info),
    SUCCESS(R.drawable.ic_toast_success, R.drawable.shape_toast_success),
    WARNING(R.drawable.ic_toast_warning, R.drawable.shape_toast_warning),
    ERROR(R.drawable.ic_toast_error, R.drawable.shape_toast_error)
}