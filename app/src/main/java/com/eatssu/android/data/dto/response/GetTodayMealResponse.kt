package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Menu
import com.google.gson.annotations.SerializedName

data class GetTodayMealResponseDto(
    @SerializedName("mealInformationResponseList") var mealInformationResponseList: ArrayList<MealInformationResponseList> = arrayListOf(),
)

data class MealInformationResponseList(

    @SerializedName("mealId") var mealId: Int? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("mainRating") var mainRating: String? = null,
    @SerializedName("menusInformation") var menusInformation: ArrayList<MenusInformation> = arrayListOf(),
)

data class MenusInformation(
    @SerializedName("menuId") var menuId: Int? = null,
    @SerializedName("name") var name: String? = null,
)

fun GetTodayMealResponseDto.mapTodayMenuResponseToMenu(): List<Menu> {
    val menuList = mutableListOf<Menu>()

    mealInformationResponseList.forEach { mealInfo ->
        val nameList = mealInfo.menusInformation.mapNotNull { it.name } // Filter out null names
        if (nameList.isNotEmpty()) {
            val name = nameList.joinToString("+")
            val menu = Menu(
                id = mealInfo.mealId ?: -1, // Default value if mealId is null
                name = name,
                price = mealInfo.price ?: 0, // Default value if price is null
                rate = mealInfo.mainRating
            )
            menuList.add(menu)
        }
    }

    return menuList
}