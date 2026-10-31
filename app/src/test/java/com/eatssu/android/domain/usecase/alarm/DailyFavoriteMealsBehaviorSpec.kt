package com.eatssu.android.domain.usecase.alarm

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDate

class DailyFavoriteMealsBehaviorSpec : AppBehaviorSpec({
    val date = LocalDate.of(2026, 11, 2)
    given("11시 맞춤 알림의 점심·저녁 식단 조회") {
        then("찜 목록이 없거나 조회에 실패하면 기본 문구로 대체할 빈 결과를 반환한다") {
            val repository = mockk<MenuFavoriteRepository>()
            coEvery { repository.getFavoriteMenus() } returns ApiResult.Success(emptyList())
            GetDailyFavoriteMealsUseCase(repository)(date) shouldBe emptyList()
            coEvery { repository.getFavoriteMenus() } returns ApiResult.Failure(500, "failed")
            GetDailyFavoriteMealsUseCase(repository)(date) shouldBe emptyList()
            coVerify(exactly = 0) { repository.getTodayMenus(any(), any(), any()) }
        }
        then("점심과 저녁에서 일치한 메뉴를 각각 묶는다") {
            val repository = mockk<MenuFavoriteRepository>()
            coEvery { repository.getFavoriteMenus() } returns ApiResult.Success(
                listOf(
                    FavoriteMenu(1, "파스타", Restaurant.DORMITORY, false),
                )
            )
            coEvery {
                repository.getTodayMenus(
                    date,
                    Restaurant.DORMITORY,
                    any()
                )
            } returns ApiResult.Success(
                listOf(
                    MenuFavoriteSearchResult(1, "파스타", Restaurant.DORMITORY, false),
                    MenuFavoriteSearchResult(9, "찜하지 않은 메뉴", Restaurant.DORMITORY, false),
                )
            )
            val matches = GetDailyFavoriteMealsUseCase(repository)(date)
            matches.map { it.time } shouldBe listOf(Time.LUNCH, Time.DINNER)
            matches.map { it.menus.map { menu -> menu.menuId } } shouldBe listOf(
                listOf(1L),
                listOf(1L)
            )
            matches.all { it.date == date && it.targetRestaurant == Restaurant.DORMITORY } shouldBe true
        }
        then("점심에는 없고 저녁에만 나오는 메뉴도 저녁 알림 대상으로 찾는다") {
            val repository = mockk<MenuFavoriteRepository>()
            coEvery { repository.getFavoriteMenus() } returns ApiResult.Success(
                listOf(
                    FavoriteMenu(
                        1,
                        "파스타",
                        Restaurant.DODAM,
                        false
                    )
                )
            )
            coEvery {
                repository.getTodayMenus(
                    date,
                    Restaurant.DODAM,
                    Time.LUNCH
                )
            } returns ApiResult.Success(emptyList())
            coEvery {
                repository.getTodayMenus(
                    date,
                    Restaurant.DODAM,
                    Time.DINNER
                )
            } returns ApiResult.Success(
                listOf(
                    MenuFavoriteSearchResult(
                        1,
                        "파스타",
                        Restaurant.DODAM,
                        false
                    )
                )
            )
            GetDailyFavoriteMealsUseCase(repository)(date).single().time shouldBe Time.DINNER
        }
        then("여러 식당이 일치하면 화면에서 가장 위 식당을 이동 대상으로 선택한다") {
            val repository = mockk<MenuFavoriteRepository>()
            coEvery { repository.getFavoriteMenus() } returns ApiResult.Success(
                listOf(
                    FavoriteMenu(2, "파스타", Restaurant.DORMITORY, false),
                    FavoriteMenu(1, "파스타", Restaurant.HAKSIK, false),
                )
            )
            coEvery { repository.getTodayMenus(any(), any(), any()) } returns ApiResult.Success(
                emptyList()
            )
            coEvery {
                repository.getTodayMenus(
                    date,
                    Restaurant.DORMITORY,
                    Time.LUNCH
                )
            } returns ApiResult.Success(
                listOf(
                    MenuFavoriteSearchResult(
                        2,
                        "파스타",
                        Restaurant.DORMITORY,
                        false
                    )
                )
            )
            coEvery {
                repository.getTodayMenus(
                    date,
                    Restaurant.HAKSIK,
                    Time.LUNCH
                )
            } returns ApiResult.Success(
                listOf(
                    MenuFavoriteSearchResult(1, "파스타", Restaurant.HAKSIK, false),
                    MenuFavoriteSearchResult(1, "중복 메뉴", Restaurant.HAKSIK, false),
                )
            )
            val match = GetDailyFavoriteMealsUseCase(repository)(date).single()
            match.menus.map { it.menuId } shouldBe listOf(1L, 2L)
            match.targetRestaurant shouldBe Restaurant.HAKSIK
            coVerify(exactly = 0) { repository.getTodayMenus(date, Restaurant.HAKSIK, Time.DINNER) }
        }
        then("미운영 시간대와 찜하지 않은 식당은 조회하지 않는다") {
            val repository = mockk<MenuFavoriteRepository>()
            coEvery { repository.getFavoriteMenus() } returns ApiResult.Success(
                listOf(
                    FavoriteMenu(
                        1,
                        "국",
                        Restaurant.FACULTY,
                        false
                    )
                )
            )
            coEvery {
                repository.getTodayMenus(
                    date,
                    Restaurant.FACULTY,
                    Time.LUNCH
                )
            } returns ApiResult.Failure(503, "failed")
            GetDailyFavoriteMealsUseCase(repository)(date) shouldBe emptyList()
            coVerify(exactly = 1) { repository.getTodayMenus(date, Restaurant.FACULTY, Time.LUNCH) }
            coVerify(exactly = 0) {
                repository.getTodayMenus(
                    date,
                    Restaurant.FACULTY,
                    Time.DINNER
                )
            }
            coVerify(exactly = 0) { repository.getTodayMenus(date, Restaurant.DODAM, any()) }
        }
    }
})
