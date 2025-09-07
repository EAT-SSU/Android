package com.eatssu.android.domain.model

data class PartnershipRestaurant(
    val id: Int,
    val partnershipType: String,
    val storeName: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val restaurantType: RestaurantType,
    val longitude: Double,
    val latitude: Double,
    val collegeName: String,
    val departmentName: String,
    val partnershipLikeCount: Int,
    val likedByUser: Boolean,
)

enum class RestaurantType {
    CAFE, RESTAURANT, PUB
}
