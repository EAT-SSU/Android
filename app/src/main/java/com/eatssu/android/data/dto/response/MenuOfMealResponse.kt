package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class MenuOfMealResponse(
    @SerializedName("menuList") var menuList: ArrayList<MenuList> = arrayListOf()
)

data class MenuList(

    @SerializedName("menuId") var menuId: Long? = null,
    @SerializedName("name") var name: String? = null

)

fun MenuOfMealResponse.toDomain(): List<Pair<Long, String>> {
    return menuList.map {
        (it.menuId ?: -1L) to (it.name ?: "")
    }
}