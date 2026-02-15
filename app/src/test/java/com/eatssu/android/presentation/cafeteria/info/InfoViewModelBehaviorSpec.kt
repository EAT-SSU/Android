package com.eatssu.android.presentation.cafeteria.info

import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class InfoViewModelBehaviorSpec : AppBehaviorSpec({

    given("식당 정보 조회") {
        val repo = mockk<FirebaseRemoteConfigRepository>()

        `when`("원격 설정 조회가 성공하면") {
            val info = RestaurantInfo(
                enum = Restaurant.HAKSIK,
                name = "학식",
                location = "1층",
                image = "img",
                time = "09:00",
                etc = "etc",
            )
            coEvery { repo.getRestaurantInfo(Restaurant.HAKSIK) } returns info
            val viewModel = InfoViewModel(repo)

            then("식당 정보를 반환한다") {
                runTest {
                    viewModel.getRestaurantInfo(Restaurant.HAKSIK) shouldBe info
                }
            }
        }

        `when`("원격 설정 조회 중 예외가 발생하면") {
            coEvery { repo.getRestaurantInfo(Restaurant.HAKSIK) } throws IllegalStateException("boom")
            val viewModel = InfoViewModel(repo)

            then("null을 반환한다") {
                runTest {
                    viewModel.getRestaurantInfo(Restaurant.HAKSIK).shouldBeNull()
                }
            }
        }
    }
})
