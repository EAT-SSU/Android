package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.PartnershipResponse
import com.eatssu.android.data.remote.dto.response.PartnershipRestaurantResponse
import com.eatssu.android.data.remote.service.PartnershipService
import com.eatssu.android.data.remote.service.UserService
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.StoreType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class PartnershipRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("PartnershipRepositoryImpl") {
        val partnershipService = mockk<PartnershipService>()
        val userService = mockk<UserService>()
        val favoritePartnershipDataStore = mockk<FavoritePartnershipDataStore>(relaxed = true)
        val repository = PartnershipRepositoryImpl(
            partnershipService,
            userService,
            favoritePartnershipDataStore,
        )

        `when`("전체 제휴 조회 API가 성공하면") {
            val response = listOf(
                PartnershipResponse(
                    storeName = "Cafe A",
                    longitude = 127.0,
                    latitude = 37.0,
                    restaurantType = StoreType.CAFE,
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
                restaurantType = StoreType.CAFE,
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
                    restaurantType = StoreType.CAFE,
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

        `when`("유저 찜 제휴 조회 API가 성공하면") {
            val response = listOf(
                PartnershipResponse(
                    storeName = "Favorite Cafe",
                    restaurantType = StoreType.CAFE,
                    partnershipInfos = emptyList(),
                )
            )
            coEvery { userService.getUserFavoritePartnerships() } returns ApiResult.Success(response)

            then("도메인 제휴 목록으로 변환한다") {
                runTest {
                    repository.getUserFavoritePartnerships()
                        .first().storeName shouldBe "Favorite Cafe"
                }
            }
        }

        `when`("새 제휴 찜 요청이 성공하면") {
            coEvery { partnershipService.likePartnership(7) } returns ApiResult.Success(Unit)

            then("최근 찜 순서의 맨 앞으로 기록한다") {
                runTest {
                    repository.likePartnership(7, wasLiked = false)
                    coVerify(exactly = 1) { favoritePartnershipDataStore.markLiked(7) }
                    coVerify(exactly = 0) { favoritePartnershipDataStore.markUnliked(any()) }
                }
            }
        }

        `when`("제휴 찜 취소 요청이 성공하면") {
            coEvery { partnershipService.likePartnership(7) } returns ApiResult.Success(Unit)

            then("최근 찜 순서에서 제거한다") {
                runTest {
                    repository.likePartnership(7, wasLiked = true)
                    coVerify(exactly = 1) { favoritePartnershipDataStore.markUnliked(7) }
                }
            }
        }
    }
})
