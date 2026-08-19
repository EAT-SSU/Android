package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.GoodPriceStoreDetail
import com.eatssu.common.enums.GoodPriceCategory
import kotlinx.serialization.Serializable

/**
 * 착한가격업소 상세 조회 응답 DTO
 */
@Serializable
data class GoodPriceStoreDetailResponse(
    val id: Long,
    val storeName: String,
    val category: String,
    val mainMenu: String? = null,
    val price: Int? = null,
    val roadAddress: String? = null,
    val imageUrl: String? = null,
)

// DTO -> Domain 상세 모델 변환
fun GoodPriceStoreDetailResponse.toDomain(): GoodPriceStoreDetail = GoodPriceStoreDetail(
    id = id,
    storeName = storeName,
    category = GoodPriceCategory.fromServerKey(category),
    mainMenu = mainMenu,
    price = price,
    roadAddress = roadAddress,
    imageUrl = imageUrl,
)
