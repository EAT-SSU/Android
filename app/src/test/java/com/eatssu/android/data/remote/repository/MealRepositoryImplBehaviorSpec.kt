package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GetMealMenusInfoResponse
import com.eatssu.android.data.remote.dto.response.GetMealResponse
import com.eatssu.android.data.remote.dto.response.MenusInformationList
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MealRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("MealRepositoryImpl") {
        val mealService = mockk<MealService>()
        val settingDataStore = mockk<SettingDataStore>()
        val appLanguage = MutableStateFlow(AppLanguage.KOREAN)
        val repository = MealRepositoryImpl(mealService, settingDataStore)

        every { settingDataStore.appLanguage } returns appLanguage

        val mealResponse = listOf(
            GetMealResponse(
                mealId = 10L,
                price = 5000,
                rating = 4.0,
                briefMenus = listOf(
                    MenusInformationList(menuId = 1L, name = "제육", isMain = true),
                    MenusInformationList(menuId = 2L, name = "계란찜"),
                ),
            )
        )

        `when`("한국어로 getTodayMeal API가 성공하면") {
            coEvery {
                mealService.getTodayMeal("2025-01-01", "HAKSIK", "LUNCH", null)
            } returns ApiResult.Success(mealResponse)

            then("language를 보내지 않고 모든 메뉴를 반환한다") {
                runTest {
                    appLanguage.value = AppLanguage.KOREAN

                    repository.getTodayMeal("2025-01-01", "HAKSIK", "LUNCH") shouldBe listOf(
                        listOf("제육", "계란찜")
                    )
                }
            }
        }

        `when`("getTodayMeal API가 실패하면") {
            coEvery {
                mealService.getTodayMeal("2025-01-02", "HAKSIK", "LUNCH", null)
            } returns ApiResult.Failure(500, "err")

            then("빈 리스트를 반환한다") {
                runTest {
                    appLanguage.value = AppLanguage.KOREAN

                    repository.getTodayMeal("2025-01-02", "HAKSIK", "LUNCH") shouldBe emptyList()
                }
            }
        }

        `when`("한국어로 getTodayMenuList API가 성공하면") {
            coEvery {
                mealService.getTodayMeal(
                    "2025-01-03",
                    Restaurant.HAKSIK.toString(),
                    Time.LUNCH.toString(),
                    null,
                )
            } returns ApiResult.Success(mealResponse)

            then("모든 메뉴를 Menu 도메인으로 변환한다") {
                runTest {
                    appLanguage.value = AppLanguage.KOREAN

                    val result = repository.getTodayMenuList(
                        "2025-01-03",
                        Restaurant.HAKSIK,
                        Time.LUNCH,
                    )
                    result.size shouldBe 1
                    result.first().name shouldBe "제육, 계란찜"
                }
            }
        }

        `when`("영어로 식단을 조회하면") {
            val translatedResponse = listOf(
                GetMealResponse(
                    mealId = 11L,
                    price = 5000,
                    rating = 4.0,
                    briefMenus = listOf(
                        MenusInformationList(menuId = 1L, name = "Spicy Pork", isMain = true),
                        MenusInformationList(menuId = 2L, name = "계란찜"),
                    ),
                )
            )
            coEvery {
                mealService.getTodayMeal("2025-01-04", "HAKSIK", "LUNCH", "EN")
            } returns ApiResult.Success(translatedResponse)

            then("language=EN을 보내고 대표메뉴만 표시한다") {
                runTest {
                    appLanguage.value = AppLanguage.ENGLISH

                    repository.getTodayMeal("2025-01-04", "HAKSIK", "LUNCH") shouldBe listOf(
                        listOf("Spicy Pork")
                    )
                }
            }
        }

        `when`("대표메뉴 번역 데이터가 없는 식단을 영어로 조회하면") {
            val untranslatedResponse = listOf(
                GetMealResponse(
                    mealId = 12L,
                    briefMenus = listOf(
                        MenusInformationList(menuId = 1L, name = "제육"),
                        MenusInformationList(menuId = 2L, name = "계란찜"),
                    ),
                )
            )
            coEvery {
                mealService.getTodayMeal("2025-01-05", "HAKSIK", "LUNCH", "EN")
            } returns ApiResult.Success(untranslatedResponse)

            then("기존 한국어 전체 메뉴를 표시한다") {
                runTest {
                    appLanguage.value = AppLanguage.ENGLISH

                    repository.getTodayMeal("2025-01-05", "HAKSIK", "LUNCH") shouldBe listOf(
                        listOf("제육", "계란찜")
                    )
                }
            }
        }

        `when`("일본어와 베트남어로 식단을 조회하면") {
            coEvery {
                mealService.getTodayMeal(any(), "HAKSIK", "LUNCH", "EN")
            } returns ApiResult.Success(mealResponse)

            then("두 언어 모두 임시로 language=EN을 보낸다") {
                runTest {
                    appLanguage.value = AppLanguage.JAPANESE
                    repository.getTodayMeal("2025-01-06", "HAKSIK", "LUNCH")

                    appLanguage.value = AppLanguage.VIETNAMESE
                    repository.getTodayMeal("2025-01-07", "HAKSIK", "LUNCH")

                    coVerify {
                        mealService.getTodayMeal("2025-01-06", "HAKSIK", "LUNCH", "EN")
                        mealService.getTodayMeal("2025-01-07", "HAKSIK", "LUNCH", "EN")
                    }
                }
            }
        }

        `when`("변동식단 상세 메뉴를 영어로 조회하면") {
            coEvery {
                mealService.getMealMenusInfo(5637L, "EN")
            } returns ApiResult.Success(
                GetMealMenusInfoResponse(
                    briefMenus = listOf(
                        MenusInformationList(name = "Beef Shabu Rice Noodles", isMain = true),
                        MenusInformationList(name = "팔춘권튀김"),
                        MenusInformationList(name = "미니밥"),
                    )
                )
            )

            then("대표메뉴 영문과 나머지 메뉴 한국어를 함께 반환한다") {
                runTest {
                    appLanguage.value = AppLanguage.ENGLISH

                    repository.getMealMenuNames(5637L) shouldBe listOf(
                        "Beef Shabu Rice Noodles",
                        "팔춘권튀김",
                        "미니밥",
                    )
                }
            }
        }
    }
})
