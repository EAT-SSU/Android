package com.eatssu.android.data.dto.response

data class PartnershipResponse(
    val storeName: String,
    val longitude: Double,
    val latitude: Double,
    val restaurantType: String,
    val partnershipInfos: List<PartnershipInfo>
){
    data class PartnershipInfo(
        val id: Int,
        val partnershipType: String,
        val collegeName: String,
        val departmentName: String,
        val likeCount: Int,
        val isLiked: Boolean,
        val description: String,
        val startDate: String,
        val endDate: String
    )
}

