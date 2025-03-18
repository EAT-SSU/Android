package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.MenuMini
import com.google.gson.annotations.SerializedName

data class MenuOfMealResponse(
    @SerializedName("briefMenus") var briefMenus: ArrayList<MenusInformation> = arrayListOf(),
)

data class MenusInformation(

    @SerializedName("menuId") var menuId: Long,
    @SerializedName("name") var name: String,

    )

fun MenuOfMealResponse.toMenuMiniList(): List<MenuMini> {
    return briefMenus.map { it.toMenuMini() }
}

fun MenusInformation.toMenuMini(): MenuMini {
    return MenuMini(
        id = this.menuId,
        name = this.name
    )
}