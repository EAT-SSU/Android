package com.eatssu.android.presentation.goodprice

import com.eatssu.android.domain.model.GoodPriceStore
import com.eatssu.android.domain.model.GoodPriceStoreDetail
import com.eatssu.common.enums.GoodPriceCategory

/**
 * 착한가격업소 지도 화면 상태
 */
data class GoodPriceMapState(
    val stores: List<GoodPriceStore> = emptyList(), // 지도에 표시할 업소 목록
    val selectedCategory: GoodPriceCategory = GoodPriceCategory.ALL, // 현재 선택된 필터 카테고리
    val selectedStore: GoodPriceStore? = null, // 딥링크에 사용할 선택 업소의 좌표 정보
    val selectedStoreDetail: GoodPriceStoreDetail? = null, // 현재 선택되어 바텀시트에 표시할 업소 상세 정보
    val isLoading: Boolean = false, // 로딩 상태
)
