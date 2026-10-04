package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.GoodPriceStore
import com.eatssu.common.enums.GoodPriceCategory
import kotlinx.serialization.Serializable

/**
 * 착한가격업소 목록 조회 응답 DTO
 */
@Serializable
data class GoodPriceStoreResponse(
    val id: Long,
    val storeName: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
)

// DTO -> Domain 모델 변환
fun GoodPriceStoreResponse.toDomain(): GoodPriceStore = GoodPriceStore(
    id = id,
    storeName = storeName,
    category = GoodPriceCategory.fromServerKey(category),
    latitude = latitude,
    longitude = longitude,
)
