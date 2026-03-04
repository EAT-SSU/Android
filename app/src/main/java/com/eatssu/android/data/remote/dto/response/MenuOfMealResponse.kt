package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.MenuMini
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuOfMealResponse(
    @SerialName("menuList") val menuList: ArrayList<MenuList> = arrayListOf()
)

@Serializable
data class MenuList(

    @SerialName("menuId") val menuId: Long? = null,
    @SerialName("name") val name: String? = null

)

fun MenuOfMealResponse.toDomain(): List<MenuMini> {
    return menuList.map {
        MenuMini(
            id = it.menuId ?: -1L,
            name = it.name ?: ""
        )
    }
}
