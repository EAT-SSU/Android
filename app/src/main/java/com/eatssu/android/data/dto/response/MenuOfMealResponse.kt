package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.MenuMini
import com.google.gson.annotations.SerializedName

data class MenuOfMealResponse(
    @SerializedName("menusInformation") var menusInformation: ArrayList<MenusInformation> = arrayListOf(),
)

data class MenusInformation(

    @SerializedName("menuId") var menuId: Long,
    @SerializedName("name") var name: String,

    )

fun MenuOfMealResponse.toMenuMiniList(): List<MenuMini> {
    return menusInformation.map { it.toMenuMini() }
}

fun MenusInformation.toMenuMini(): MenuMini {
    return MenuMini(
        id = this.menuId,
        name = this.name
    )
}