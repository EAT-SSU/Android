package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class PartnershipRestaurantResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("partnershipType")
    val partnershipType: String,
    @SerializedName("storeName")
    val storeName: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("restaurantType")
    val restaurantType: String,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("collegeName")
    val collegeName: String,
    @SerializedName("departmentName")
    val departmentName: String?,
    @SerializedName("partnershipLikeCount")
    val partnershipLikeCount: Int,
    @SerializedName("likedByUser")
    val likedByUser: Boolean,
)
