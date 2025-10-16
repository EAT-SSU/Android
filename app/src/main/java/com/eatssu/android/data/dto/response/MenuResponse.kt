package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Menu
import com.google.gson.annotations.SerializedName
import timber.log.Timber


data class GetFixedMenuResponse(

    @SerializedName("categoryMenuListCollection") val categoryMenuListCollection: ArrayList<CategoryMenuListCollection> = arrayListOf(),

    )

data class CategoryMenuListCollection(

    @SerializedName("category") val category: String? = null,
    @SerializedName("menus") val menus: ArrayList<MenuInformationList> = arrayListOf(),

    )

data class MenuInformationList(

    @SerializedName("menuId") var menuId: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("rating") val rating: Double? = null,

    )


fun GetFixedMenuResponse.mapFixedMenuResponseToMenu(): List<Menu> {
    val menus = mutableListOf<Menu>()

    categoryMenuListCollection.forEach { categoryMenuList ->
        val categoryName = categoryMenuList.category ?: ""
        categoryMenuList.menus.forEach { menuInfo ->
            val menu = Menu(
                id = menuInfo.menuId ?: 0,
                name = menuInfo.name ?: "",
                price = menuInfo.price ?: 0,
                rate = menuInfo.rating ?: 0.0
            )
            menus.add(menu)
        }
    }
    Timber.d(menus.toString())

    return menus
}
