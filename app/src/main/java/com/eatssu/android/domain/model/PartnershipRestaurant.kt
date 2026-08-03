package com.eatssu.android.domain.model

import com.eatssu.common.enums.StoreType

data class PartnershipRestaurant(
    val id: Int,
    val partnershipType: String,
    val storeName: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val storeType: StoreType,
    val longitude: Double,
    val latitude: Double,
    val collegeName: String,
    val departmentName: String,
    val partnershipLikeCount: Int,
    val likedByUser: Boolean,
    val naverMapUrl: String? = null,
    val kakaoMapUrl: String? = null,
)
