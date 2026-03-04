package com.eatssu.android.domain.model

import com.eatssu.common.enums.StoreType

data class Partnership(
    val storeName: String,
    val longitude: Double,
    val latitude: Double,
    val restaurantType: StoreType,
    val partnershipInfos: List<PartnershipInfo>
) {
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