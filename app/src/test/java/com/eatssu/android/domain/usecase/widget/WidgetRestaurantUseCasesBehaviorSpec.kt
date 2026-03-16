package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.data.local.WidgetDataStore
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetRestaurantUseCasesBehaviorSpec : AppBehaviorSpec({

    given("LoadRestaurantByFileKeyUseCase") {
        val widgetDataStore = mockk<WidgetDataStore>()
        val useCase = LoadRestaurantByFileKeyUseCase(widgetDataStore)

        `when`("저장된 식당이 있으면") {
            coEvery { widgetDataStore.loadRestaurantByFileKey("file-key") } returns Restaurant.HAKSIK

            then("식당 enum을 반환한다") {
                runTest {
                    useCase("file-key") shouldBe Restaurant.HAKSIK
                }
            }
        }

        `when`("저장된 식당이 없으면") {
            coEvery { widgetDataStore.loadRestaurantByFileKey("file-key") } returns null

            then("null을 반환한다") {
                runTest {
                    useCase("file-key") shouldBe null
                }
            }
        }
    }

    given("SaveRestaurantByFileKeyUseCase") {
        val widgetDataStore = mockk<WidgetDataStore>()
        val useCase = SaveRestaurantByFileKeyUseCase(widgetDataStore)
        coJustRun { widgetDataStore.saveRestaurantByFileKey(any(), any()) }

        `when`("fileKey와 식당을 전달하면") {
            then("저장소에 그대로 위임한다") {
                runTest {
                    useCase("file-key", Restaurant.DODAM)
                    coVerify(exactly = 1) { widgetDataStore.saveRestaurantByFileKey("file-key", Restaurant.DODAM) }
                }
            }
        }
    }
})
