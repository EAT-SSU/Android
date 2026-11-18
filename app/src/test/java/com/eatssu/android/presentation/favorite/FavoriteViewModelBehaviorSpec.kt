package com.eatssu.android.presentation.favorite

import com.eatssu.android.R
import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnership
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.StoreType
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FavoriteViewModelBehaviorSpec : AppBehaviorSpec({

    given("제휴 찜 화면") {
        val repository = mockk<PartnershipRepository>()
        val dataStore = mockk<FavoritePartnershipDataStore>()
        val menuRepository = mockk<MenuFavoriteRepository>()
        val restaurant = samplePartnership(storeName = "식당", type = StoreType.RESTAURANT)
        val cafe = samplePartnership(
            storeName = "카페",
            infos = restaurant.partnershipInfos.map { it.copy(id = 2) },
            type = StoreType.CAFE,
        )

        coEvery { repository.getUserFavoritePartnerships() } returns listOf(restaurant, cafe)
        coEvery { dataStore.reconcile(listOf(1, 2)) } returns listOf(2, 1)
        coEvery { menuRepository.getFavoriteMenus() } returns ApiResult.Success(emptyList())

        val viewModel = FavoriteViewModel(repository, dataStore, menuRepository)

        `when`("찜 목록을 불러오면") {
            viewModel.loadFavorites()

            then("기기에 기록한 최근순으로 화면 상태를 만든다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.partnerships.map { it.partnershipId } shouldBe listOf(2, 1)
                state.partnerships.map { it.storeName } shouldBe listOf("카페", "식당")
            }
        }

        `when`("음식점 필터를 선택하면") {
            viewModel.loadFavorites()
            viewModel.selectStoreType(StoreType.RESTAURANT)

            then("음식점만 노출한다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.filteredPartnerships.map { it.storeName } shouldBe listOf("식당")
            }
        }

        `when`("찜 해제를 요청하면") {
            coEvery {
                repository.likePartnership(
                    1,
                    wasLiked = true
                )
            } returns com.eatssu.android.data.model.ApiResult.Success(Unit)
            viewModel.loadFavorites()
            viewModel.removeFavorite(1)

            then("해당 항목을 목록에서 제거한다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.partnerships.map { it.partnershipId } shouldBe listOf(2)
            }
        }
    }

    given("메뉴 찜 화면") {
        val partnershipRepository = mockk<PartnershipRepository>()
        val dataStore = mockk<FavoritePartnershipDataStore>()
        val menuRepository = mockk<MenuFavoriteRepository>()
        val favoriteMenu = FavoriteMenu(
            menuId = 10L,
            menuName = "김치찌개",
            restaurant = Restaurant.HAKSIK,
            isDiscontinued = false,
        )
        val searchResult = MenuFavoriteSearchResult(
            menuId = 20L,
            menuName = "김치닭볶음탕",
            restaurant = Restaurant.DODAM,
            isFavorite = false,
        )

        coEvery { partnershipRepository.getUserFavoritePartnerships() } returns emptyList()
        coEvery { dataStore.reconcile(emptyList()) } returns emptyList()
        coEvery { menuRepository.getFavoriteMenus() } returns ApiResult.Success(listOf(favoriteMenu))
        coEvery { menuRepository.searchMenus("김치") } returns ApiResult.Success(listOf(searchResult))
        coEvery { menuRepository.addFavoriteMenu(20L) } returns ApiResult.Success(Unit)

        val viewModel = FavoriteViewModel(partnershipRepository, dataStore, menuRepository)

        `when`("찜 목록을 불러오면") {
            viewModel.loadFavorites()

            then("서버가 내려준 최신순 메뉴를 표시한다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.favoriteMenus shouldBe listOf(favoriteMenu)
            }
        }

        `when`("공백을 제외한 검색어가 두 글자 미만이면") {
            viewModel.loadFavorites()
            viewModel.onMenuSearchQueryChanged(" 김 ")
            viewModel.submitMenuSearch()

            then("서버 검색을 호출하지 않는다") {
                coVerify(exactly = 0) { menuRepository.searchMenus(any()) }
            }
        }

        `when`("두 글자 이상 메뉴를 검색하면") {
            viewModel.loadFavorites()
            viewModel.onMenuSearchQueryChanged(" 김치 ")
            viewModel.submitMenuSearch()

            then("앞뒤 공백을 제거한 결과를 식당 정보와 함께 유지한다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.menuSearchResults shouldBe listOf(searchResult)
                state.menuSearchResults.first().restaurant shouldBe Restaurant.DODAM
            }
        }

        `when`("검색 결과를 찜하면") {
            viewModel.loadFavorites()
            viewModel.onMenuSearchQueryChanged("김치")
            viewModel.submitMenuSearch()
            viewModel.toggleMenuFavorite(searchResult)

            then("찜 목록의 맨 앞에 추가하고 검색 결과도 갱신한다") {
                val state = (viewModel.uiState.value as UiState.Success).data
                state.favoriteMenus shouldHaveSize 2
                state.favoriteMenus.first().menuId shouldBe 20L
                state.menuSearchResults.first().isFavorite shouldBe true
            }
        }
    }

    given("찜 목록에서 메뉴 하트를 눌러 삭제할 때") {
        val partnershipRepository = mockk<PartnershipRepository>()
        val dataStore = mockk<FavoritePartnershipDataStore>()
        val menuRepository = mockk<MenuFavoriteRepository>()
        val favoriteMenu = FavoriteMenu(
            menuId = 10L,
            menuName = "김치찌개",
            restaurant = Restaurant.HAKSIK,
            isDiscontinued = false,
        )

        coEvery { partnershipRepository.getUserFavoritePartnerships() } returns emptyList()
        coEvery { dataStore.reconcile(emptyList()) } returns emptyList()
        coEvery { menuRepository.getFavoriteMenus() } returns ApiResult.Success(listOf(favoriteMenu))
        coEvery { menuRepository.removeFavoriteMenu(10L) } returns ApiResult.Success(Unit)

        val viewModel = FavoriteViewModel(partnershipRepository, dataStore, menuRepository)

        `when`("하트를 누르면") {
            val events = mutableListOf<FavoriteMenuSnackbarEvent>()
            val scope = CoroutineScope(Dispatchers.Unconfined)
            val job = scope.launch { viewModel.menuSnackbarEvents.collect { events += it } }
            try {
                viewModel.loadFavorites()
                viewModel.toggleFavoriteMenu(favoriteMenu)

                then("삭제 토스트와 함께 리스트에서 즉시 삭제된다") {
                    val state = (viewModel.uiState.value as UiState.Success).data
                    state.favoriteMenus shouldBe emptyList()
                    state.unfavoritedMenuIds shouldBe emptySet()
                    events.map { it.messageRes } shouldBe listOf(R.string.favorite_menu_deleted_snackbar)
                    coVerify(exactly = 1) { menuRepository.removeFavoriteMenu(10L) }
                }
            } finally {
                job.cancel()
                scope.cancel()
            }
        }
    }

    given("이미 불러온 찜 목록이 있는 상태에서 재조회할 때") {
        val partnershipRepository = mockk<PartnershipRepository>()
        val dataStore = mockk<FavoritePartnershipDataStore>()
        val menuRepository = mockk<MenuFavoriteRepository>()
        val favoriteMenu = FavoriteMenu(
            menuId = 10L,
            menuName = "김치찌개",
            restaurant = Restaurant.HAKSIK,
            isDiscontinued = false,
        )

        coEvery { partnershipRepository.getUserFavoritePartnerships() } returns emptyList()
        coEvery { dataStore.reconcile(emptyList()) } returns emptyList()
        coEvery { menuRepository.getFavoriteMenus() } returnsMany listOf(
            ApiResult.Success(listOf(favoriteMenu)),
            ApiResult.Failure(500, "network"),
            ApiResult.Success(emptyList()),
        )

        val viewModel = FavoriteViewModel(partnershipRepository, dataStore, menuRepository)

        `when`("재조회가 실패한 뒤 다시 조회하면") {
            viewModel.loadFavorites()
            viewModel.loadFavorites()

            then("일시적 재조회 실패 시에는 기존 목록을 유지하고 성공 시 서버 상태로 갱신한다") {
                var state = (viewModel.uiState.value as UiState.Success).data
                state.favoriteMenus shouldBe listOf(favoriteMenu)

                viewModel.loadFavorites()
                state = (viewModel.uiState.value as UiState.Success).data
                state.favoriteMenus shouldBe emptyList()
            }
        }
    }
})
