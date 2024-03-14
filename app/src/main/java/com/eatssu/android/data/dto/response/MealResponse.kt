package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Menu
import com.google.gson.annotations.SerializedName

data class GetMealResponse(

    @SerializedName("mealId") var mealId: Long? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("mainRating") var mainRating: Double? = null,
    @SerializedName("menusInformationList") var menusInformationList: ArrayList<MenusInformationList> = arrayListOf(),
)

data class MenusInformationList(

    @SerializedName("menuId") var menuId: Long? = null,
    @SerializedName("name") var name: String? = null,

    )

fun ArrayList<GetMealResponse>.mapTodayMenuResponseToMenu(): List<Menu> {
    val menuList = mutableListOf<Menu>()

    this.forEach { mealResponse ->
        val menuNames =
            mealResponse.menusInformationList.joinToString(separator = "+") { it.name ?: "" }
        val mealId = mealResponse.mealId ?: -1
        val price = mealResponse.price ?: 0
        val mainRating = mealResponse.mainRating ?: 0.0

        val menu = Menu(mealId, menuNames, price, mainRating)

        menuList.add(menu)
    }

    return menuList
}