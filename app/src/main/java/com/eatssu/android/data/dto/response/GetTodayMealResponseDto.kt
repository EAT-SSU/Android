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

fun GetTodayMealResponseDto.createNameList(): String {
    val nameList = StringBuilder()

    for (mealInfo in mealInformationResponseList) {
        for (menuInfo in mealInfo.menusInformation) {
            nameList.append(menuInfo.name)
            nameList.append("+")
        }
    }

    if (nameList.isNotEmpty()) {
        nameList.deleteCharAt(nameList.length - 1) // Remove the last '+'
    }

    return nameList.toString()
}

fun GetTodayMealResponseDto.mapTodayMenuResponseToMenu(): List<Menu> {
    return mealInformationResponseList.map { mealInfo ->
        val name = createNameList()
//        if (name.isNotEmpty()) {
        Menu(
            id = mealInfo.mealId ?: -1, // Default value if mealId is null
            name = name,
            price = mealInfo.price ?: 0, // Default value if price is null
            rate = mealInfo.mainRating
        )
//        }
    }
}