package com.eatssu.android.data.model.response

import com.google.gson.annotations.SerializedName


typealias GetChangedMenuInfoResponse = List<MenuList>;

data class MenuList(
    val flag: Int,
    val menuInfoList: List<MenuInfo>
) {
    data class MenuInfo(
        val grade: Double,
        val menuId: Int,
        val name: String,
        val price: Int
    )
}
