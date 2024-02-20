package com.eatssu.android.data.dto.response

data class GetMenuInfoListResponseDto(
    val menuInfoList: List<MenuInfo>
){
    data class MenuInfo(
        val grade: Double,
        val menuId: Int,
        val name: String,
        val price: Int
    )
}