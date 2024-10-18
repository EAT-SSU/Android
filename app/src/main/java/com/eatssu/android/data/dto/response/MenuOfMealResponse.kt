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

fun MenuOfMealResponse.asMenuOfMeal(): List<MenuMini> {
    val menuList = mutableListOf<MenuMini>()

    this.menusInformation.forEach {

        val menu = MenuMini(it.menuId, it.name)
        menuList.add(menu)
    }

    return menuList
}