package com.eatssu.android.presentation.favorite

import com.eatssu.android.R
import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MenuFavoriteSearchBehaviorSpec : AppBehaviorSpec({
    given("엔터로 요청하는 메뉴 검색") {
        then("포커스와 타이핑은 요청하지 않고 한 글자는 엔터를 눌러도 요청하지 않는다") {
            val fixture = SearchFixture()
            val vm = fixture.viewModel()
            vm.onMenuSearchFocusChanged(true)
            vm.onMenuSearchQueryChanged("m")
            vm.submitMenuSearch()
            vm.onMenuSearchQueryChanged("me")
            vm.onMenuSearchQueryChanged("meat")
            coVerify(exactly = 0) { fixture.repository.searchMenus(any()) }
            vm.state().isMenuSearchMode shouldBe true
            vm.state().hasSubmittedMenuSearch shouldBe false
            vm.submitMenuSearch()
            coVerify(exactly = 1) { fixture.repository.searchMenus("meat") }
            vm.state().hasSubmittedMenuSearch shouldBe true
        }
        then("요청 중 엔터 중복 입력은 합치고 바뀐 검색어에는 이전 결과를 반영하지 않는다") {
            val fixture = SearchFixture()
            val pending = CompletableDeferred<ApiResult<List<MenuFavoriteSearchResult>>>()
            coEvery { fixture.repository.searchMenus("김치") } coAnswers { pending.await() }
            val vm = fixture.viewModel()
            vm.onMenuSearchQueryChanged("김치")
            vm.submitMenuSearch()
            vm.submitMenuSearch()
            coVerify(exactly = 1) { fixture.repository.searchMenus("김치") }
            vm.onMenuSearchQueryChanged("파스타")
            pending.complete(ApiResult.Success(listOf(searchMenu(1, "김치찌개"))))
            vm.state().menuSearchResults shouldBe emptyList()
            vm.state().menuSearchLoadState shouldBe MenuFavoriteLoadState.Idle
            coVerify(exactly = 0) { fixture.repository.searchMenus("파스타") }
        }
        then("X로 닫으면 검색을 지우고 아코디언을 접는다") {
            val vm = SearchFixture().viewModel()
            vm.toggleTodayMenus()
            vm.onMenuSearchQueryChanged("김치")
            vm.submitMenuSearch()
            vm.exitMenuSearch()
            vm.state().isMenuSearchMode shouldBe false
            vm.state().menuSearchQuery shouldBe ""
            vm.state().menuSearchResults shouldBe emptyList()
            vm.todayMenuState.value.expanded shouldBe false
        }
        then("앞에 키워드가 오는 메뉴를 우선하고 나머지는 가나다순이며 기존 찜도 유지한다") {
            val fixture = SearchFixture()
            coEvery { fixture.repository.searchMenus("김치") } returns ApiResult.Success(
                listOf(
                    searchMenu(1, "포기김치"), searchMenu(2, "배추김치", true),
                    searchMenu(3, "뚝배기목살김치찌개"), searchMenu(4, "김치닭볶음탕"),
                )
            )
            val vm = fixture.viewModel()
            vm.onMenuSearchQueryChanged(" 김치 ")
            vm.submitMenuSearch()
            vm.state().menuSearchResults.map { it.menuId } shouldBe listOf(4L, 3L, 2L, 1L)
            vm.state().menuSearchResults.first { it.menuId == 2L }.isFavorite shouldBe true
            sortMenuFavoriteSearchResults(
                listOf(
                    searchMenu(5, "Steamed meat"),
                    searchMenu(6, "Meatballs")
                ), "me"
            )
                .map { it.menuId } shouldBe listOf(6L, 5L)
        }
        then("추가와 삭제는 서버 성공 후 하트를 바꾸고 각각 성공 알림을 보낸다") {
            val fixture = SearchFixture()
            val item = searchMenu(1, "Meatballs")
            coEvery { fixture.repository.searchMenus("me") } returns ApiResult.Success(listOf(item))
            coEvery { fixture.repository.addFavoriteMenu(1) } returns ApiResult.Success(Unit)
            coEvery { fixture.repository.removeFavoriteMenu(1) } returns ApiResult.Success(Unit)
            val vm = fixture.viewModel()
            val events = mutableListOf<FavoriteMenuSnackbarEvent>()
            val scope = CoroutineScope(Dispatchers.Unconfined)
            val job = scope.launch { vm.menuSnackbarEvents.collect { events += it } }
            try {
                vm.onMenuSearchQueryChanged("me")
                vm.submitMenuSearch()
                vm.toggleMenuFavorite(vm.state().menuSearchResults.single())
                vm.state().menuSearchResults.single().isFavorite shouldBe true
                vm.toggleMenuFavorite(vm.state().menuSearchResults.single())
                vm.state().menuSearchResults.single().isFavorite shouldBe false
                events.map { it.messageRes } shouldBe listOf(
                    R.string.favorite_menu_added_snackbar,
                    R.string.favorite_menu_deleted_snackbar
                )
            } finally {
                job.cancel(); scope.cancel()
            }
        }
        then("검색과 식단 조회 실패는 메뉴 로드 오류 알림을 보낸다") {
            val fixture = SearchFixture()
            coEvery { fixture.repository.searchMenus(any()) } returns ApiResult.Failure(
                500,
                "failed"
            )
            coEvery {
                fixture.repository.getTodayMenus(
                    any(),
                    any(),
                    any()
                )
            } returns ApiResult.Failure(500, "failed")
            val vm = fixture.viewModel()
            val events = mutableListOf<FavoriteMenuSnackbarEvent>()
            val scope = CoroutineScope(Dispatchers.Unconfined)
            val job = scope.launch { vm.menuSnackbarEvents.collect { events += it } }
            try {
                vm.onMenuSearchQueryChanged("김치")
                vm.submitMenuSearch()
                vm.loadTodayMenus()
                events.map { it.messageRes } shouldBe listOf(
                    R.string.favorite_menu_load_error,
                    R.string.favorite_menu_load_error
                )
                events.all { it.isError } shouldBe true
            } finally {
                job.cancel(); scope.cancel()
            }
        }
    }
})

private fun searchMenu(id: Long, name: String, favorite: Boolean = false) =
    MenuFavoriteSearchResult(id, name, Restaurant.HAKSIK, favorite)

private fun FavoriteViewModel.state() = (uiState.value as UiState.Success).data
private class SearchFixture {
    val repository = mockk<MenuFavoriteRepository>()
    private val partnerships = mockk<PartnershipRepository>()
    private val order = mockk<FavoritePartnershipDataStore>()

    init {
        coEvery { partnerships.getUserFavoritePartnerships() } returns emptyList()
        coEvery { order.reconcile(emptyList()) } returns emptyList()
        coEvery { repository.getFavoriteMenus() } returns ApiResult.Success(emptyList())
        coEvery { repository.searchMenus(any()) } returns ApiResult.Success(emptyList())
        coEvery { repository.getTodayMenus(any(), any(), any()) } returns ApiResult.Success(
            emptyList()
        )
    }

    fun viewModel() = FavoriteViewModel(partnerships, order, repository).also { it.loadFavorites() }
}
