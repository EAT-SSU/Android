package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName


//data class GetFixedMenuResponseDto(
//    val fixMenuInfoList: List<FixMenuInfoList>,
//)
data class FixMenuInfoList(
    @SerializedName("menuId"     ) var menuId     : Int?    = null,
    @SerializedName("name"       ) var name       : String? = null,
    @SerializedName("price"      ) var price      : Int?    = null,
    @SerializedName("mainRating" ) var mainRating : String? = null
)

