package com.eatssu.android.domain.usecase.goodprice

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.GoodPriceStoreDetail
import com.eatssu.android.domain.repository.GoodPriceStoreRepository
import javax.inject.Inject

/**
 * 착한가격업소 상세 정보 조회 유스케이스
 */
class GetGoodPriceStoreDetailUseCase @Inject constructor(
    private val repository: GoodPriceStoreRepository,
) {
    suspend operator fun invoke(id: Long): ApiResult<GoodPriceStoreDetail> {
        return repository.getStoreDetail(id)
    }
}
