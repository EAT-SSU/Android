package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GetMealResponse
import com.eatssu.android.data.remote.dto.response.MenusInformationList
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.data.remote.service.MenuFavoriteService
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class TodayMenuFavoritesRepositoryBehaviorSpec : AppBehaviorSpec({
    given("식단의 개별 메뉴를 찜할 때") {
        then("식단 ID 대신 개별 메뉴 ID를 사용하고 중복·불완전 항목을 제외한다") {
            val meals = mockk<MealService>()
            coEvery {
                meals.getTodayMeal(
                    "20261031",
                    "HAKSIK",
                    "LUNCH",
                    null
                )
            } returns ApiResult.Success(
                listOf(
                    GetMealResponse(
                        mealId = 99, briefMenus = listOf(
                            MenusInformationList(menuId = 1, name = "미역국"),
                            MenusInformationList(menuId = 1, name = "미역국"),
                            MenusInformationList(menuId = null, name = "없는 ID"),
                            MenusInformationList(menuId = 2, name = " "),
                        )
                    )
                ),
            )
            val settings = mockk<SettingDataStore>()
            io.mockk.every { settings.appLanguage } returns flowOf(AppLanguage.KOREAN)
            val repository =
                MenuFavoriteRepositoryImpl(mockk<MenuFavoriteService>(), meals, settings)
            val result =
                repository.getTodayMenus(LocalDate.of(2026, 10, 31), Restaurant.HAKSIK, Time.LUNCH)
            (result as ApiResult.Success).data.map { it.menuId } shouldBe listOf(1L)
            result.data.single().restaurant shouldBe Restaurant.HAKSIK
        }
        then("다국어 모드에서는 서버의 영어 메뉴명을 조회한다") {
            val meals = mockk<MealService>()
            coEvery {
                meals.getTodayMeal(
                    "20261031",
                    "HAKSIK",
                    "LUNCH",
                    "EN"
                )
            } returns ApiResult.Success(
                listOf(
                    GetMealResponse(
                        mealId = 99,
                        briefMenus = listOf(
                            MenusInformationList(
                                menuId = 1,
                                name = "Roasted chicken with vegetables"
                            )
                        )
                    )
                ),
            )
            val settings = mockk<SettingDataStore>()
            io.mockk.every { settings.appLanguage } returns flowOf(AppLanguage.ENGLISH)
            val repository =
                MenuFavoriteRepositoryImpl(mockk<MenuFavoriteService>(), meals, settings)
            val result =
                repository.getTodayMenus(LocalDate.of(2026, 10, 31), Restaurant.HAKSIK, Time.LUNCH)
            (result as ApiResult.Success).data.single().menuName shouldBe "Roasted chicken with vegetables"
        }
    }
})
