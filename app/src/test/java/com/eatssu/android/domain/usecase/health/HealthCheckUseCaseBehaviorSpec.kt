package com.eatssu.android.domain.usecase.health

import com.eatssu.android.domain.repository.HealthCheckRepository
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class HealthCheckUseCaseBehaviorSpec : AppBehaviorSpec({

    given("HealthCheckUseCase") {
        val repository = mockk<HealthCheckRepository>()
        val useCase = HealthCheckUseCase(repository)

        `when`("헬스체크가 성공하면") {
            coEvery { repository.checkHealth() } returns true

            then("true를 반환한다") {
                runTest {
                    useCase() shouldBe true
                    coVerify(exactly = 1) { repository.checkHealth() }
                }
            }
        }

        `when`("헬스체크가 실패하면") {
            coEvery { repository.checkHealth() } returns false

            then("false를 반환한다") {
                runTest {
                    useCase() shouldBe false
                }
            }
        }
    }
})
