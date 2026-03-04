package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.remote.service.HealthCheckService
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HealthCheckRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("HealthCheckRepositoryImpl") {
        val service = mockk<HealthCheckService>()
        val repository = HealthCheckRepositoryImpl(service)

        `when`("health check API가 성공하면") {
            coEvery { service.checkHealth() } returns Response.success(Unit)

            then("true를 반환한다") {
                runTest {
                    repository.checkHealth() shouldBe true
                }
            }
        }

        `when`("health check API가 실패하면") {
            coEvery { service.checkHealth() } returns Response.error(
                500,
                "error".toResponseBody("text/plain".toMediaType()),
            )

            then("false를 반환한다") {
                runTest {
                    repository.checkHealth() shouldBe false
                }
            }
        }
    }
})
