package com.eatssu.android.domain.model

import com.eatssu.common.enums.GoodPriceCategory

/**
 * 착한가격업소 상세 정보 도메인 모델 (바텀시트 노출용)
 */
data class GoodPriceStoreDetail(
    val id: Long,
    val storeName: String,
    val category: GoodPriceCategory,
    val mainMenu: String?,
    val price: Int?,
    val roadAddress: String?,
    val imageUrl: String?,
)
