package com.eatssu.android.data.model.response


data class GetFixedMenuResponseDto(
    val fixMenuInfoList: List<FixMenuInfoList>,
){
    data class FixMenuInfoList(
        val mainRating: String,
        val menuId: Long,
        val name: String,
        val price: Long
    )
}

