package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.PartnershipResponse
import com.eatssu.android.data.remote.dto.response.PartnershipRestaurantResponse
import com.eatssu.android.data.remote.service.PartnershipService
import com.eatssu.android.data.remote.service.UserService
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class PartnershipRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("PartnershipRepositoryImpl") {
        val partnershipService = mockk<PartnershipService>()
        val userService = mockk<UserService>()
        val repository = PartnershipRepositoryImpl(partnershipService, userService)

        `when`("전체 제휴 조회 API가 성공하면") {
            val response = listOf(
                PartnershipResponse(
                    storeName = "Cafe A",
                    longitude = 127.0,
                    latitude = 37.0,
                    restaurantType = "CAFE",
                    partnershipInfos = listOf(
                        PartnershipResponse.PartnershipInfo(
                            id = 1,
                            partnershipType = "DISCOUNT",
                            collegeName = "IT",
                            departmentName = "CS",
                            likeCount = 1,
                            isLiked = true,
                            description = "10% 할인",
                            startDate = "2025-01-01",
                            endDate = "2025-12-31",
                        )
                    ),
                )
            )
            coEvery { partnershipService.getAllPartnerships() } returns ApiResult.Success(response)

            then("도메인 Partnership 리스트를 반환한다") {
                runTest {
                    val result = repository.getAllPartnerships()
                    result.size shouldBe 1
                    result.first().storeName shouldBe "Cafe A"
                }
            }
        }

        `when`("전체 제휴 조회 API가 실패하면") {
            coEvery { partnershipService.getAllPartnerships() } returns ApiResult.Failure(500, "err")

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getAllPartnerships() shouldBe emptyList()
                }
            }
        }

        `when`("개별 제휴 조회 API가 성공하면") {
            val response = PartnershipRestaurantResponse(
                id = 1,
                partnershipType = "DISCOUNT",
                storeName = "Cafe A",
                description = "10% 할인",
                startDate = "2025-01-01",
                endDate = "2025-12-31",
                restaurantType = "CAFE",
                longitude = 127.0,
                latitude = 37.0,
                collegeName = "IT",
                departmentName = "CS",
                partnershipLikeCount = 1,
                likedByUser = true,
            )
            coEvery { partnershipService.getPartnershipById(1) } returns ApiResult.Success(response)

            then("도메인 PartnershipRestaurant를 반환한다") {
                runTest {
                    val result = repository.getPartnershipById(1)
                    result?.id shouldBe 1
                    result?.storeName shouldBe "Cafe A"
                    result?.description shouldBe "10% 할인"
                    result?.collegeName shouldBe "IT"
                }
            }
        }

        `when`("개별 제휴 조회 API가 실패하면") {
            coEvery { partnershipService.getPartnershipById(1) } returns ApiResult.UnknownError(IllegalStateException("boom"))

            then("null을 반환한다") {
                runTest {
                    repository.getPartnershipById(1) shouldBe null
                }
            }
        }

        `when`("유저 학과 제휴 조회가 실패하면") {
            coEvery { userService.getUserDepartmentPartnerships() } returns ApiResult.Failure(500, "err")

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getUserCollegePartnerships() shouldBe emptyList()
                }
            }
        }

        `when`("유저 학과 제휴 조회가 성공하면") {
            val response = listOf(
                PartnershipResponse(
                    storeName = "Cafe B",
                    longitude = 127.0,
                    latitude = 37.0,
                    restaurantType = "CAFE",
                    partnershipInfos = emptyList(),
                )
            )
            coEvery { userService.getUserDepartmentPartnerships() } returns ApiResult.Success(response)

            then("도메인 Partnership 리스트를 반환한다") {
                runTest {
                    val result = repository.getUserCollegePartnerships()
                    result.size shouldBe 1
                    result.first().storeName shouldBe "Cafe B"
                }
            }
        }
    }
})
