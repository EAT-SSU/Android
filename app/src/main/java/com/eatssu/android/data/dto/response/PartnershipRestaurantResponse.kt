package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.model.RestaurantType
import com.google.gson.annotations.SerializedName

data class PartnershipRestaurantResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("partnershipType")
    val partnershipType: String?,
    @SerializedName("storeName")
    val storeName: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("startDate")
    val startDate: String?,
    @SerializedName("endDate")
    val endDate: String?,
    @SerializedName("restaurantType")
    val restaurantType: String?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("collegeName")
    val collegeName: String?,
    @SerializedName("departmentName")
    val departmentName: String?,
    @SerializedName("partnershipLikeCount")
    val partnershipLikeCount: Int?,
    @SerializedName("likedByUser")
    val likedByUser: Boolean?,
)

fun PartnershipRestaurantResponse.toDomain(): PartnershipRestaurant =
    PartnershipRestaurant(
        id = id ?: -1,
        partnershipType = partnershipType ?: "",
        storeName = storeName ?: "",
        description = description ?: "",
        startDate = startDate ?: "",
        endDate = endDate ?: "",
        restaurantType = restaurantType ?.let {
            when (it) {
                "CAFE" -> RestaurantType.CAFE
                "RESTAURANT" -> RestaurantType.RESTAURANT
                "PUB" -> RestaurantType.PUB
                else -> RestaurantType.RESTAURANT
            }
        } ?: RestaurantType.RESTAURANT,
        longitude = longitude ?: 126.95661313346206,
        latitude = latitude ?: 37.49517278813046,
        collegeName = collegeName ?: "",
        departmentName = departmentName ?: "",
        partnershipLikeCount = partnershipLikeCount ?: 0,
        likedByUser = likedByUser ?: false
    )