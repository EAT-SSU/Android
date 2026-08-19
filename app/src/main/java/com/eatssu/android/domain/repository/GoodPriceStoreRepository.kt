package com.eatssu.android.domain.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.GoodPriceStore
import com.eatssu.android.domain.model.GoodPriceStoreDetail
import com.eatssu.common.enums.GoodPriceCategory

/**
 * 착한가격업소 저장소 인터페이스
 */
interface GoodPriceStoreRepository {

    // 업종별 착한가격업소 목록 조회
    suspend fun getStores(category: GoodPriceCategory?): ApiResult<List<GoodPriceStore>>

    // 착한가격업소 상세 정보 조회
    suspend fun getStoreDetail(id: Long): ApiResult<GoodPriceStoreDetail>
}
