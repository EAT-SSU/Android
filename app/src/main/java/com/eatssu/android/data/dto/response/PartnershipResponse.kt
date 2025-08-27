package com.eatssu.android.data.dto.response

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

