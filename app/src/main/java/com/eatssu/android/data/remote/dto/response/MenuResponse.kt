package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Menu
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber


@Serializable
data class GetFixedMenuResponse(

    @SerialName("categoryMenuListCollection") val categoryMenuListCollection: ArrayList<CategoryMenuListCollection> = arrayListOf(),

    )

@Serializable
data class CategoryMenuListCollection(

    @SerialName("category") val category: String? = null,
    @SerialName("menus") val menus: ArrayList<MenuInformationList> = arrayListOf(),

    )

@Serializable
data class MenuInformationList(

    @SerialName("menuId") val menuId: Long? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("price") val price: Int? = null,
    @SerialName("rating") val rating: Double? = null,

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
