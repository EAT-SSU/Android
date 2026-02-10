package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Menu
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val MENU_SEPARATOR = ", "

@Serializable
data class GetMealResponse(
    @SerialName("mealId") var mealId: Long? = null,
    @SerialName("price") var price: Int? = null,
    @SerialName("rating") var rating: Double? = null,
    @SerialName("briefMenus") var briefMenus: List<MenusInformationList> = emptyList(),
)

@Serializable
data class MenusInformationList(
    @SerialName("menuId") var menuId: Long? = null,
    @SerialName("name") var name: String? = null,
)

fun List<GetMealResponse>.mapTodayMenuResponseToMenu(): List<Menu> {
    val menuList = mutableListOf<Menu>()

    this.forEach { mealResponse ->
        val menuNames =
            mealResponse.briefMenus.mapNotNull { it.name }.joinToString(separator = MENU_SEPARATOR)
        val mealId = mealResponse.mealId ?: -1
        val price = mealResponse.price ?: 0
        val mainRating = mealResponse.rating ?: 0.0

        val menu = Menu(mealId, menuNames, price, mainRating)

        menuList.add(menu)
    }

    return menuList
}


fun List<GetMealResponse>.toDomain(): List<List<String>> {
    return this.map { meal ->
        meal.briefMenus.mapNotNull { it.name }
    }
}
