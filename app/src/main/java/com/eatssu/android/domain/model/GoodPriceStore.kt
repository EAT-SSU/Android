package com.eatssu.android.domain.model

import com.eatssu.common.enums.GoodPriceCategory

/**
 * 착한가격업소 지도 핀 표시용 도메인 모델
 */
data class GoodPriceStore(
    val id: Long,
    val storeName: String,
    val category: GoodPriceCategory,
    val latitude: Double,
    val longitude: Double,
)
