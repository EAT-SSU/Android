package com.eatssu.android.domain.usecase.goodprice

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.GoodPriceStore
import com.eatssu.android.domain.repository.GoodPriceStoreRepository
import com.eatssu.common.enums.GoodPriceCategory
import javax.inject.Inject

/**
 * 카테고리별 착한가격업소 목록 조회 유스케이스
 */
class GetGoodPriceStoresUseCase @Inject constructor(
    private val repository: GoodPriceStoreRepository,
) {
    suspend operator fun invoke(category: GoodPriceCategory? = GoodPriceCategory.ALL): ApiResult<List<GoodPriceStore>> {
        return repository.getStores(category)
    }
}
