package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.model.map
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.GoodPriceStoreService
import com.eatssu.android.domain.model.GoodPriceStore
import com.eatssu.android.domain.model.GoodPriceStoreDetail
import com.eatssu.android.domain.repository.GoodPriceStoreRepository
import com.eatssu.common.enums.GoodPriceCategory
import javax.inject.Inject

/**
 * 착한가격업소 저장소 구현체
 */
class GoodPriceStoreRepositoryImpl @Inject constructor(
    private val service: GoodPriceStoreService,
) : GoodPriceStoreRepository {

    // 필터 카테고리에 맞는 업소 목록 조회 (ALL이거나 null이면 전체 조회)
    override suspend fun getStores(category: GoodPriceCategory?): ApiResult<List<GoodPriceStore>> {
        val serverKey = if (category == GoodPriceCategory.ALL) null else category?.serverKey
        return service.getStores(category = serverKey).map { list ->
            list.map { it.toDomain() }
        }
    }

    // 업소 상세 정보 조회
    override suspend fun getStoreDetail(id: Long): ApiResult<GoodPriceStoreDetail> {
        return service.getStoreDetail(id).map { it.toDomain() }
    }
}
