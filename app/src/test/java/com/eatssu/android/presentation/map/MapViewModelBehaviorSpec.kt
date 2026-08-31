package com.eatssu.android.presentation.map

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.usecase.user.GetPartnershipDetailUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.presentation.map.component.PartnershipCategory
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnership
import com.eatssu.android.test.samplePartnershipRestaurant
import com.eatssu.android.test.sampleUserInfo
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.PeriodType
import com.eatssu.common.enums.StoreType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelBehaviorSpec : AppBehaviorSpec({

    given("제휴 지도 화면") {
        val partnershipRepository = mockk<PartnershipRepository>()
        val getPartnershipDetailUseCase = mockk<GetPartnershipDetailUseCase>()
        val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
        val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

        `when`("학과 정보가 없을 때") {
            val allPartnerships = listOf(samplePartnership(storeName = "All Cafe"))
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = -1, collegeName = "단과대"),
                department = Department(departmentId = -1, departmentName = "학과"),
            )
            coEvery { partnershipRepository.getAllPartnerships() } returns allPartnerships
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns emptyList()

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("전체 카테고리로 시작하고 학과 입력 필요 상태와 빈 마커 목록을 유지한다") {
                runTest {
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.selectedCategory shouldBe PartnershipCategory.ALL
                        state.data.filterChangeResult shouldBe MapState.FilterChangeResult.RequiresDepartment
                        state.data.partnerships shouldBe emptyList()
                    }
                    coVerify(exactly = 0) { partnershipRepository.getUserCollegePartnerships() }
                }
            }
        }

        `when`("Festival 제휴가 있으면") {
            val festivalInfo = Partnership.PartnershipInfo(
                id = 1,
                partnershipType = "DISCOUNT",
                collegeName = "IT",
                departmentName = "CS",
                likeCount = 1,
                isLiked = false,
                description = "축제 할인",
                startDate = "2025-05-01",
                endDate = "2025-05-03",
                periodType = PeriodType.FESTIVAL,
            )
            val normalInfo = Partnership.PartnershipInfo(
                id = 2,
                partnershipType = "DISCOUNT",
                collegeName = "IT",
                departmentName = "CS",
                likeCount = 1,
                isLiked = false,
                description = "상시 할인",
                startDate = "2025-01-01",
                endDate = "2025-12-31",
                periodType = PeriodType.NORMAL,
            )
            val allPartnerships = listOf(
                samplePartnership(
                    storeName = "Festival Cafe",
                    infos = listOf(festivalInfo, normalInfo),
                )
            )
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getAllPartnerships() } returns allPartnerships
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns allPartnerships

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("별도 Festival 필터 없이 전체 카테고리에 포함한다") {
                runTest {
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.selectedCategory shouldBe PartnershipCategory.ALL
                        state.data.partnerships.first().partnershipInfos shouldBe listOf(
                            festivalInfo,
                            normalInfo,
                        )
                    }
                }
            }
        }

        `when`("학과 정보가 없는 사용자가 카테고리를 변경하면") {
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = -1, collegeName = "단과대"),
                department = Department(departmentId = -1, departmentName = "학과"),
            )
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns emptyList()

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("선택 카테고리와 학과 입력 필요 상태를 유지하고 데이터를 로드하지 않는다") {
                runTest {
                    eventually(2.seconds) {
                        viewModel.uiState.value shouldBe UiState.Success(
                            MapState(
                                selectedCategory = PartnershipCategory.ALL,
                                filterChangeResult = MapState.FilterChangeResult.RequiresDepartment,
                            ),
                        )
                    }
                    viewModel.setCategory(PartnershipCategory.CAFE)

                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.selectedCategory shouldBe PartnershipCategory.CAFE
                        state.data.filterChangeResult shouldBe MapState.FilterChangeResult.RequiresDepartment
                        state.data.partnerships shouldBe emptyList()
                    }
                    coVerify(exactly = 0) { partnershipRepository.getUserCollegePartnerships() }
                }
            }
        }

        `when`("학과 정보가 있는 사용자가 카테고리를 변경하면") {
            val restaurant = samplePartnership(
                storeName = "Restaurant",
                type = StoreType.RESTAURANT,
            )
            val cafe = samplePartnership(
                storeName = "Cafe",
                type = StoreType.CAFE,
            )
            val pub = samplePartnership(
                storeName = "Pub",
                type = StoreType.PUB,
            )
            val partnerships = listOf(restaurant, cafe, pub)
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("학과 제휴 목록을 장소 유형별로 필터링한다") {
                runTest {
                    eventually(2.seconds) {
                        val initial = viewModel.uiState.value as UiState.Success
                        initial.data.selectedCategory shouldBe PartnershipCategory.ALL
                        initial.data.partnerships shouldBe partnerships
                        initial.data.visiblePartnerships shouldBe partnerships
                    }

                    viewModel.setCategory(PartnershipCategory.CAFE)
                    eventually(2.seconds) {
                        val cafeState = viewModel.uiState.value as UiState.Success
                        cafeState.data.selectedCategory shouldBe PartnershipCategory.CAFE
                        cafeState.data.visiblePartnerships shouldBe listOf(cafe)
                    }

                    viewModel.setCategory(PartnershipCategory.RESTAURANT)
                    eventually(2.seconds) {
                        val restaurantState = viewModel.uiState.value as UiState.Success
                        restaurantState.data.selectedCategory shouldBe PartnershipCategory.RESTAURANT
                        restaurantState.data.visiblePartnerships shouldBe listOf(restaurant)
                    }
                }
            }
        }

        `when`("가게를 선택하면") {
            val partnershipInfos = listOf(
                Partnership.PartnershipInfo(
                    id = 1,
                    partnershipType = "DISCOUNT",
                    collegeName = "IT",
                    departmentName = "CS",
                    likeCount = 2,
                    isLiked = true,
                    description = "10% 할인",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31",
                    periodType = PeriodType.NORMAL,
                ),
                Partnership.PartnershipInfo(
                    id = 2,
                    partnershipType = "DISCOUNT",
                    collegeName = "IT",
                    departmentName = "CS",
                    likeCount = 3,
                    isLiked = false,
                    description = "음료 증정",
                    startDate = "2025-02-01",
                    endDate = "2025-11-30",
                    periodType = PeriodType.NORMAL,
                ),
            )
            val partnerships = listOf(
                samplePartnership(
                    storeName = "Cafe A",
                    infos = partnershipInfos,
                    type = StoreType.PUB,
                )
            )
            val representative = samplePartnershipRestaurant(
                id = 2,
                type = StoreType.PUB,
            )

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            every {
                getPartnershipDetailUseCase(partnerships, "Cafe A", 2)
            } returns representative

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("대표 제휴와 표시용 리스트/장소 타입을 상태에 반영한다") {
                runTest {
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.partnerships shouldBe partnerships
                    }

                    viewModel.selectPartnershipByStoreName("Cafe A", 2)
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.restaurantPartnershipInfo shouldBe representative
                        state.data.storeType shouldBe StoreType.PUB
                        state.data.restaurantInfoList.size shouldBe 2
                        state.data.restaurantInfoList[1].period shouldBe "2025-02-01 ~ 2025-11-30"
                    }
                }
            }
        }

        `when`("초기 상태가 Init일 때 카테고리를 선택하면") {
            coEvery {
                getUserCollegeDepartmentUseCase()
            } coAnswers {
                delay(10_000)
                sampleUserInfo(
                    nickname = "eatssu",
                    college = College(collegeId = -1, collegeName = "단과대"),
                    department = Department(departmentId = -1, departmentName = "학과"),
                )
            }

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("상태를 변경하지 않고 반환한다") {
                viewModel.setCategory(PartnershipCategory.CAFE)
                viewModel.uiState.value shouldBe UiState.Init
                coVerify(exactly = 0) { partnershipRepository.getUserCollegePartnerships() }
            }
        }

        `when`("제휴 목록에 없는 가게를 선택하면") {
            val partnerships = listOf(samplePartnership(storeName = "Cafe A"))
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("선택 상태를 갱신하지 않는다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe partnerships
                    }

                    viewModel.selectPartnershipByStoreName("Unknown")
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.restaurantPartnershipInfo shouldBe null
                        state.data.restaurantInfoList shouldBe emptyList()
                    }
                }
            }
        }

        `when`("제휴 상세에서 representative를 찾지 못하면") {
            val partnerships = listOf(
                samplePartnership(
                    storeName = "Cafe A",
                    infos = listOf(
                        Partnership.PartnershipInfo(
                            id = 1,
                            partnershipType = "DISCOUNT",
                            collegeName = "IT",
                            departmentName = "CS",
                            likeCount = 1,
                            isLiked = true,
                            description = "할인",
                            startDate = "2025-01-01",
                            endDate = "2025-12-31",
                            periodType = PeriodType.NORMAL,
                        )
                    ),
                )
            )
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            every { getPartnershipDetailUseCase(partnerships, "Cafe A", 1) } returns null

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("선택 상태를 갱신하지 않는다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe partnerships
                    }

                    viewModel.selectPartnershipByStoreName("Cafe A")
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.restaurantPartnershipInfo shouldBe null
                    }
                }
            }
        }

        `when`("대표 제휴 타입이 CAFE면") {
            val partnerships = listOf(samplePartnership(storeName = "Cafe C", type = StoreType.CAFE))
            val representative = samplePartnershipRestaurant(type = StoreType.CAFE)

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            every { getPartnershipDetailUseCase(partnerships, "Cafe C", 1) } returns representative

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("StoreType.CAFE로 변환한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe partnerships
                    }
                    viewModel.selectPartnershipByStoreName("Cafe C")

                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.storeType shouldBe StoreType.CAFE
                    }
                }
            }
        }

        `when`("대표 제휴 타입이 RESTAURANT면") {
            val partnerships = listOf(samplePartnership(storeName = "Restaurant A", type = StoreType.RESTAURANT))
            val representative = samplePartnershipRestaurant(type = StoreType.RESTAURANT)

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "컴퓨터학부"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            every { getPartnershipDetailUseCase(partnerships, "Restaurant A", 1) } returns representative

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("StoreType.RESTAURANT로 변환한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe partnerships
                    }
                    viewModel.selectPartnershipByStoreName("Restaurant A")

                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.storeType shouldBe StoreType.RESTAURANT
                    }
                }
            }
        }

        `when`("a partnership sheet is dismissed and the same store is selected again") {
            val partnerships = listOf(samplePartnership(storeName = "Nolsoop"))
            val representative = samplePartnershipRestaurant(type = StoreType.CAFE)

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "CS"),
            )
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns partnerships
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            every { getPartnershipDetailUseCase(partnerships, "Nolsoop", 1) } returns representative

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("the selection state is cleared before reopening the same store") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe partnerships
                    }

                    viewModel.selectPartnershipByStoreName("Nolsoop")
                    (viewModel.uiState.value as UiState.Success)
                        .data.restaurantPartnershipInfo shouldBe representative

                    viewModel.clearSelectedPartnership()
                    with((viewModel.uiState.value as UiState.Success).data) {
                        restaurantPartnershipInfo shouldBe null
                        restaurantInfoList shouldBe emptyList()
                        storeType shouldBe null
                    }

                    viewModel.selectPartnershipByStoreName("Nolsoop")
                    (viewModel.uiState.value as UiState.Success)
                        .data.restaurantPartnershipInfo shouldBe representative
                }
            }
        }

        `when`("제휴 좋아요 요청이 성공하면") {
            val partnership = samplePartnership(
                storeName = "Cafe Like",
                infos = listOf(
                    Partnership.PartnershipInfo(
                        id = 7,
                        partnershipType = "DISCOUNT",
                        collegeName = "IT",
                        departmentName = "CS",
                        likeCount = 3,
                        isLiked = false,
                        description = "할인",
                        startDate = "2025-01-01",
                        endDate = "2025-12-31",
                        periodType = PeriodType.NORMAL,
                    )
                ),
            )
            val representative = samplePartnershipRestaurant(id = 7).copy(
                likedByUser = false,
                partnershipLikeCount = 3,
            )

            coEvery { getUserCollegeDepartmentUseCase() } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "CS"),
            )
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns listOf(
                partnership
            )
            coEvery { partnershipRepository.likePartnership(7, false) } returns ApiResult.Success(
                Unit
            )
            every {
                getPartnershipDetailUseCase(listOf(partnership), "Cafe Like", 7)
            } returns representative

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("목록과 현재 열린 상세의 좋아요 상태를 함께 갱신한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe listOf(
                            partnership
                        )
                    }
                    viewModel.selectPartnershipByStoreName("Cafe Like", 7)
                    viewModel.likePartnership(7)

                    eventually(2.seconds) {
                        val state = (viewModel.uiState.value as UiState.Success).data
                        state.partnerships.first().partnershipInfos.first().isLiked shouldBe true
                        state.partnerships.first().partnershipInfos.first().likeCount shouldBe 4
                        state.restaurantPartnershipInfo?.likedByUser shouldBe true
                        state.restaurantPartnershipInfo?.partnershipLikeCount shouldBe 4
                    }
                    coVerify(exactly = 1) { partnershipRepository.likePartnership(7, false) }
                }
            }
        }

        `when`("제휴 좋아요 요청이 실패하면") {
            val partnership = samplePartnership(storeName = "Cafe Failed")

            coEvery { getUserCollegeDepartmentUseCase() } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = 1, collegeName = "IT"),
                department = Department(departmentId = 11, departmentName = "CS"),
            )
            coEvery { partnershipRepository.getAllPartnerships() } returns emptyList()
            coEvery { partnershipRepository.getUserCollegePartnerships() } returns listOf(
                partnership
            )
            coEvery {
                partnershipRepository.likePartnership(1, true)
            } returns ApiResult.Failure(500, "error")

            val viewModel = MapViewModel(
                partnershipRepository = partnershipRepository,
                getPartnershipDetailUseCase = getPartnershipDetailUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                analyticsTracker = analyticsTracker,
            )

            then("기존 로컬 상태를 유지한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe listOf(
                            partnership
                        )
                    }
                    viewModel.likePartnership(1)

                    eventually(2.seconds) {
                        (viewModel.uiState.value as UiState.Success).data.partnerships shouldBe listOf(
                            partnership
                        )
                    }
                }
            }
        }
    }
})
