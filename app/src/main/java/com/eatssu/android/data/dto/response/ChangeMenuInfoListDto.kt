package com.eatssu.android.data.dto.response

data class ChangeMenuInfoListDto(
    val menuInfoList: List<ChangeMenuInfo>,
)

data class ChangeMenuInfo(
    val menusInformation: List<MenuInfo>,
)

data class MenuInfo(
    val menuId: Long,
    val name: String,
)
