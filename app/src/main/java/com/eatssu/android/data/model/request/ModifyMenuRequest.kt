package com.eatssu.android.data.model.request

data class ModifyMenuRequest(
    val addTodayMenuList: List<AddTodayMenu>
){
    data class AddTodayMenu(
        val name: String,
        val price: Int
    )
}