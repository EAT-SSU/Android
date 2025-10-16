package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class MenuOfMealResponse(
    @SerializedName("menuList") val menuList: ArrayList<MenuList> = arrayListOf()
)

data class MenuList(

    @SerializedName("menuId") val menuId: Long? = null,
    @SerializedName("name") val name: String? = null

)

fun MenuOfMealResponse.toDomain(): List<Pair<Long, String>> {
    return menuList.map {
        (it.menuId ?: -1L) to (it.name ?: "")
    }
}