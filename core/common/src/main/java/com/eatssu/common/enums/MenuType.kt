package com.eatssu.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MenuType (val displayName: String){
    FIXED("고정메뉴"),
    VARIABLE("가변메뉴")
}
