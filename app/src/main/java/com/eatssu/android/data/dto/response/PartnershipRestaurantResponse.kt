package com.eatssu.android.data.dto.response

data class PartnershipRestaurantResponse(
    val id: Int,
    val partnershipType: String,
    val storeName: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val restaurantType: String,
    val longitude: Double,
    val latitude: Double,
    val collegeName: String,
    val departmentName: String,
    val partnershipLikeCount: Int,
    val likedByUser: Boolean,
)
