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
        val price = mealResponse.price ?: -1
        val mainRating = mealResponse.mainRating ?: -1.0

        val menu = Menu(mealId, menuNames, price, mainRating)

        menuList.add(menu)
    }

    return menuList
}

//fun GetTodayMealResponse.combineMenuInformation(): List<Menu> {
//    val menus = mutableListOf<Menu>()
//    menusInformationList.forEach { menuInfo ->
//        val menu = Menu(menuInfo.name, menuInfo.menuId)
//        menus.add(menu)
//    }
//    return menus
//
//fun GetTodayMealResponseDto.mapTodayMenuResponseToMenu(): List<Menu> {
//    val menuList = mutableListOf<Menu>()
//
//    mealInformationResponseList.forEach { mealInfo ->
//        val nameList = mealInfo.menusInformation.mapNotNull { it.name } // Filter out null names
//        if (nameList.isNotEmpty()) {
//            val name = nameList.joinToString("+")
//            val menu = Menu(
//                id = mealInfo.mealId ?: -1, // Default value if mealId is null
//                name = name,
//                price = mealInfo.price ?: 0, // Default value if price is null
//                rate = mealInfo.mainRating ?: ""
//            )
//            menuList.add(menu)
//        }
//    }
//
//    return menuList
