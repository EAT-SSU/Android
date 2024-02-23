package com.eatssu.android.data.dto.response

data class ChangeMenuInfo (
    val menusInformation: List<MenuInfo>
){
    data class MenuInfo(
        val menuId: Long,
        val name: String,
    )
}