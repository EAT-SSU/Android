package com.eatssu.android.presentation.favorite

import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnership
import com.eatssu.common.UiState
import com.eatssu.common.enums.StoreType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class FavoriteViewModelBehaviorSpec : AppBehaviorSpec({

    given("제휴 찜 화면") {
        val repository = mockk<PartnershipRepository>()
        val dataStore = mockk<FavoritePartnershipDataStore>()
        val restaurant = samplePartnership(storeName = "식당", type = StoreType.RESTAURANT)
        val cafe = samplePartnership(
            storeName = "카페",
            infos = restaurant.partnershipInfos.map { it.copy(id = 2) },
            type = StoreType.CAFE,
        )

        coEvery { repository.getUserFavoritePartnerships() } returns listOf(restaurant, cafe)
        coEvery { dataStore.reconcile(listOf(1, 2)) } returns listOf(2, 1)

        val viewModel = FavoriteViewModel(repository, dataStore)

        `when`("찜 목록을 불러오면") {
            viewModel.loadFavorites()

            then("기기에 기록한 최근순으로 화면 상태를 만든다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.partnerships.map { it.partnershipId } shouldBe listOf(2, 1)
                state.partnerships.map { it.storeName } shouldBe listOf("카페", "식당")
            }
        }

        `when`("음식점 필터를 선택하면") {
            viewModel.loadFavorites()
            viewModel.selectStoreType(StoreType.RESTAURANT)

            then("음식점만 노출한다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.filteredPartnerships.map { it.storeName } shouldBe listOf("식당")
            }
        }
    }
})
