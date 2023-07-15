package com.eatssu.android.data.model.response


data class GetFixedMenuResponse(
    val fixMenuInfoList: List<FixMenuInfoList>,
){
    data class FixMenuInfoList(
        val menuId: Long,
        val name: String,
        val mainGrade: Double,
        val price: Long,
    )

}

