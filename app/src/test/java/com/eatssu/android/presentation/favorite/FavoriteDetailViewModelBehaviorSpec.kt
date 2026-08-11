package com.eatssu.android.presentation.favorite

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnershipRestaurant
import com.eatssu.common.UiState
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class FavoriteDetailViewModelBehaviorSpec : AppBehaviorSpec({

    given("제휴 찜 상세 화면") {
        val repository = mockk<PartnershipRepository>()
        val restaurant = samplePartnershipRestaurant(id = 7)
        coEvery { repository.getPartnershipById(7) } returns restaurant
        coEvery { repository.likePartnership(7, true) } returns ApiResult.Success(Unit)
        val viewModel = FavoriteDetailViewModel(repository)

        `when`("상세에서 찜을 취소하면") {
            viewModel.loadPartnership(7)
            viewModel.toggleLike()

            then("서버 토글 후 상세 하트를 비운다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.likedByUser shouldBe false
                state.partnershipLikeCount shouldBe 2
                coVerify(exactly = 1) { repository.likePartnership(7, true) }
            }
        }
    }
})
