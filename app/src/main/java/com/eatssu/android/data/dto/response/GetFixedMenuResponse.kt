package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Menu
import com.google.gson.annotations.SerializedName


data class FixMenuInfoList(
    @SerializedName("menuId") var menuId: Long? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("mainRating") var mainRating: String? = null,
)

fun ArrayList<FixMenuInfoList>.mapFixedMenuResponseToMenu(): List<Menu> {
    return this.map { fixMenuInfo ->
        Menu(
            id = fixMenuInfo.menuId ?: 0,
            name = fixMenuInfo.name ?: "",
            price = fixMenuInfo.price ?: 0,
            rate = fixMenuInfo.mainRating ?: ""
        )
    }
}