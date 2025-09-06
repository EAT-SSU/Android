package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.RestaurantType
import com.google.gson.annotations.SerializedName

data class PartnershipResponse(
    @SerializedName("storeName")
    val storeName: String?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("restaurantType")
    val restaurantType: String?,
    @SerializedName("partnershipInfos")
    val partnershipInfos: List<PartnershipInfo>
){
    data class PartnershipInfo(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("partnershipType")
        val partnershipType: String?,
        @SerializedName("collegeName")
        val collegeName: String?,
        @SerializedName("departmentName")
        val departmentName: String?,
        @SerializedName("likeCount")
        val likeCount: Int?,
        @SerializedName("isLiked")
        val isLiked: Boolean?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("startDate")
        val startDate: String?,
        @SerializedName("endDate")
        val endDate: String?
    )
}

fun PartnershipResponse.toDomain(): Partnership =
    Partnership(
        storeName = storeName ?: "",
        longitude = longitude ?: 126.95661313346206,
        latitude = latitude ?: 37.49517278813046,
        restaurantType = restaurantType ?.let {
            when(it) {
                "CAFE" -> RestaurantType.CAFE
                "RESTAURANT" -> RestaurantType.RESTAURANT
                "PUB" -> RestaurantType.PUB
                else -> RestaurantType.RESTAURANT
            }
        } ?: RestaurantType.RESTAURANT,
        partnershipInfos = partnershipInfos.map {
            Partnership.PartnershipInfo(
                id = it.id ?: -1,
                partnershipType = it.partnershipType ?: "",
                collegeName = it.collegeName ?: "",
                departmentName = it.departmentName ?: "",
                likeCount = it.likeCount ?: 0,
                isLiked = it.isLiked ?: false,
                description = it.description ?: "",
                startDate = it.startDate ?: "",
                endDate = it.endDate ?: ""
            )
        }
    )