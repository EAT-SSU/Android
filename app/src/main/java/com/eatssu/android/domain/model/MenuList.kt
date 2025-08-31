package com.eatssu.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuList(
    val menuName: String,
    val menuId: Long
) : Parcelable