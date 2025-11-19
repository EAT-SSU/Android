package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.MenuMini
import com.google.gson.annotations.SerializedName

data class MenuOfMealResponse(
    @SerializedName("menuList") val menuList: ArrayList<MenuList> = arrayListOf()
)

data class MenuList(

    @SerializedName("menuId") val menuId: Long? = null,
    @SerializedName("name") val name: String? = null

)

fun MenuOfMealResponse.toDomain(): List<MenuMini> {
    return menuList.map {
        MenuMini(
            id = it.menuId ?: -1L,
            name = it.name ?: ""
        )
    }
}
