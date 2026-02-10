package com.eatssu.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class StoreType(val value: String) {
    CAFE("카페"),
    RESTAURANT("음식점"),
    PUB("주점"),
}
