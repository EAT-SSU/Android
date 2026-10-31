package com.eatssu.android.presentation.favorite

import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDate

class TodayMenuFavoritesBehaviorSpec : AppBehaviorSpec({
    given("오늘 식단에서 찜하기") {
        then("찜 탭은 학생식당 점심으로 접혀 있고 펼칠 때만 식단을 요청한다") {
            val fixture = TodayFavoriteFixture()
            val vm = fixture.viewModel()
            vm.configureTodayMenus(null, null, false)
            vm.todayMenuState.value.restaurant shouldBe Restaurant.HAKSIK
            vm.todayMenuState.value.time shouldBe Time.LUNCH
            vm.todayMenuState.value.expanded shouldBe false
            coVerify(exactly = 0) { fixture.menus.getTodayMenus(any(), any(), any()) }
            vm.toggleTodayMenus()
            vm.todayMenuState.value.menus.map { it.menuId } shouldBe listOf(1L, 2L)
            coVerify(exactly = 1) {
                fixture.menus.getTodayMenus(
                    LocalDate.now(),
                    Restaurant.HAKSIK,
                    Time.LUNCH
                )
            }
        }
        then("리뷰 바로가기는 전달받은 식당과 시간대로 펼치고 초기화를 반복해도 유지한다") {
            val vm = TodayFavoriteFixture().viewModel()
            vm.configureTodayMenus(Restaurant.DODAM, Time.DINNER, true)
            vm.configureTodayMenus(null, null, false)
            vm.todayMenuState.value.restaurant shouldBe Restaurant.DODAM
            vm.todayMenuState.value.time shouldBe Time.DINNER
            vm.todayMenuState.value.expanded shouldBe true
        }
        then("미운영 시간대 변경은 무시하고 유효한 변경 시 이전 메뉴 선택을 지운다") {
            val vm = TodayFavoriteFixture().viewModel()
            vm.configureTodayMenus(null, null, true)
            vm.toggleTodayMenu(1)
            vm.changeTodayMeal(Restaurant.HAKSIK, Time.DINNER)
            vm.todayMenuState.value.selectedMenuIds shouldBe setOf(1L)
            vm.changeTodayMeal(Restaurant.DODAM, Time.DINNER)
            vm.todayMenuState.value.restaurant shouldBe Restaurant.DODAM
            vm.todayMenuState.value.selectedMenuIds shouldBe emptySet()
        }
        then("부분 실패 시 성공 메뉴만 찜 목록에 추가하고 실패 메뉴 선택을 유지한다") {
            val fixture = TodayFavoriteFixture()
            coEvery { fixture.menus.addFavoriteMenu(1) } returns ApiResult.Success(Unit)
            coEvery { fixture.menus.addFavoriteMenu(2) } returns ApiResult.Failure(500, "failed")
            val vm = fixture.viewModel()
            vm.configureTodayMenus(null, null, true)
            vm.toggleTodayMenu(1)
            vm.toggleTodayMenu(2)
            vm.saveTodayMenus()
            (vm.uiState.value as UiState.Success).data.favoriteMenus.map { it.menuId } shouldBe listOf(
                1L
            )
            vm.todayMenuState.value.selectedMenuIds shouldBe setOf(2L)
            vm.todayMenuState.value.isSaving shouldBe false
            vm.toggleTodayMenu(1)
            vm.todayMenuState.value.selectedMenuIds shouldBe setOf(2L)
        }
        then("같은 화면에서 두 메뉴를 저장하고 두 메뉴를 더 고르면 새 메뉴만 서버에 추가한다") {
            val fixture = TodayFavoriteFixture()
            coEvery { fixture.menus.getTodayMenus(any(), any(), any()) } returns ApiResult.Success(
                (1L..4L).map { MenuFavoriteSearchResult(it, "메뉴 $it", Restaurant.HAKSIK, false) },
            )
            coEvery { fixture.menus.addFavoriteMenu(any()) } returns ApiResult.Success(Unit)
            val vm = fixture.viewModel()
            vm.configureTodayMenus(null, null, true)
            vm.toggleTodayMenu(1)
            vm.toggleTodayMenu(2)
            vm.saveTodayMenus()
            vm.todayMenuState.value.selectedMenuIds shouldBe emptySet()
            vm.toggleTodayMenu(3)
            vm.toggleTodayMenu(4)
            vm.saveTodayMenus()
            (vm.uiState.value as UiState.Success).data.favoriteMenus.map { it.menuId }
                .toSet() shouldBe setOf(1L, 2L, 3L, 4L)
            (1L..4L).forEach { id -> coVerify(exactly = 1) { fixture.menus.addFavoriteMenu(id) } }
            vm.todayMenuState.value.selectedMenuIds shouldBe emptySet()
        }
        then("메뉴 선택 없이 저장하면 등록 요청을 보내지 않는다") {
            val fixture = TodayFavoriteFixture()
            val vm = fixture.viewModel()
            vm.configureTodayMenus(null, null, true)
            vm.saveTodayMenus()
            coVerify(exactly = 0) { fixture.menus.addFavoriteMenu(any()) }
        }
        then("식단 조회 실패를 표시하고 재시도로 복구한다") {
            val fixture = TodayFavoriteFixture()
            coEvery { fixture.menus.getTodayMenus(any(), any(), any()) } returns ApiResult.Failure(
                503,
                "unavailable"
            )
            val vm = fixture.viewModel()
            vm.configureTodayMenus(null, null, true)
            vm.todayMenuState.value.loadState shouldBe MenuFavoriteLoadState.Error
            coEvery { fixture.menus.getTodayMenus(any(), any(), any()) } returns ApiResult.Success(
                emptyList()
            )
            vm.loadTodayMenus()
            vm.todayMenuState.value.loadState shouldBe MenuFavoriteLoadState.Success
            vm.todayMenuState.value.menus shouldBe emptyList()
        }
        then("식당별 운영 시간 정책과 지원 범위를 지킨다") {
            val expected = mapOf(
                Restaurant.HAKSIK to setOf(Time.MORNING, Time.LUNCH),
                Restaurant.DODAM to setOf(Time.LUNCH, Time.DINNER),
                Restaurant.DORMITORY to setOf(Time.LUNCH, Time.DINNER),
                Restaurant.FACULTY to setOf(Time.LUNCH),
            )
            Restaurant.entries.forEach { restaurant ->
                Time.entries.filter { isTodayMealAvailable(restaurant, it) }
                    .toSet() shouldBe expected[restaurant].orEmpty()
            }
            isTodayMealAvailable(null, Time.LUNCH) shouldBe false
            isTodayMealAvailable(Restaurant.HAKSIK, null) shouldBe false
        }
    }
})

private class TodayFavoriteFixture {
    val menus = mockk<MenuFavoriteRepository>()
    private val partnerships = mockk<PartnershipRepository>()
    private val order = mockk<FavoritePartnershipDataStore>()

    init {
        coEvery { partnerships.getUserFavoritePartnerships() } returns emptyList()
        coEvery { order.reconcile(emptyList()) } returns emptyList()
        coEvery { menus.getFavoriteMenus() } returns ApiResult.Success(emptyList())
        coEvery { menus.getTodayMenus(any(), any(), any()) } answers {
            ApiResult.Success(
                listOf(
                    MenuFavoriteSearchResult(1, "파스타", secondArg(), false),
                    MenuFavoriteSearchResult(2, "미역국", secondArg(), false),
                )
            )
        }
    }

    fun viewModel() = FavoriteViewModel(partnerships, order, menus).also { it.loadFavorites() }
}
