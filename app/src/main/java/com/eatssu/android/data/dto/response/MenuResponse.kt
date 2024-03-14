package com.eatssu.android.data.dto.response

import android.util.Log
import com.eatssu.android.data.model.Menu
import com.google.gson.annotations.SerializedName


data class GetFixedMenuResponse(

    @SerializedName("categoryMenuListCollection") var categoryMenuListCollection: ArrayList<CategoryMenuListCollection> = arrayListOf(),

    )

data class CategoryMenuListCollection(

    @SerializedName("category") var category: String? = null,
    @SerializedName("menuInformationList") var menuInformationList: ArrayList<MenuInformationList> = arrayListOf(),

    )

data class MenuInformationList(

    @SerializedName("menuId") var menuId: Long? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("mainRating") var mainRating: Double? = null,

    )

//fun GetFixedMenuResponse.mapFixedMenuResponseToMenu(): List<Menu> {
//    return this.map { fixMenuInfo ->
//        Menu(
//            id = fixMenuInfo.menuId ?: 0,
//            name = fixMenuInfo.name ?: "",
//            price = fixMenuInfo.price ?: 0,
//            rate = fixMenuInfo.mainRating ?: 0.0
//        )
//    }
//}


fun GetFixedMenuResponse.mapFixedMenuResponseToMenu(): List<Menu> {
    val menus = mutableListOf<Menu>()

    categoryMenuListCollection.forEach { categoryMenuList ->
        val categoryName = categoryMenuList.category ?: ""
        categoryMenuList.menuInformationList.forEach { menuInfo ->
            val menu = Menu(
                id = menuInfo.menuId ?: 0,
                name = menuInfo.name ?: "",
                price = menuInfo.price ?: 0,
                rate = menuInfo.mainRating ?: 0.0
            )
            menus.add(menu)
        }
    }
    Log.d("mapFixedMenuResponseToMenu", menus.toString())

    return menus
}
