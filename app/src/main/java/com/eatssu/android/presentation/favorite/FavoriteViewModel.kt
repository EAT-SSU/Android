package com.eatssu.android.presentation.favorite

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.common.UiState
import com.eatssu.common.enums.StoreType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MENU_SEARCH_MIN_LENGTH = 2

data class FavoritePartnershipItem(
    val partnershipId: Int,
    val storeName: String,
    val storeType: StoreType,
    val description: String,
    val detail: PartnershipRestaurant? = null,
)

enum class MenuFavoriteLoadState {
    Idle,
    Loading,
    Success,
    Error,
}

data class FavoriteState(
    val partnerships: List<FavoritePartnershipItem> = emptyList(),
    val selectedStoreType: StoreType? = null,
    val favoriteMenus: List<FavoriteMenu> = emptyList(),
    val menuSearchResults: List<MenuFavoriteSearchResult> = emptyList(),
    val menuSearchQuery: String = "",
    val favoriteMenuLoadState: MenuFavoriteLoadState = MenuFavoriteLoadState.Idle,
    val menuSearchLoadState: MenuFavoriteLoadState = MenuFavoriteLoadState.Idle,
) {
    val filteredPartnerships: List<FavoritePartnershipItem>
        get() = selectedStoreType?.let { type ->
            partnerships.filter { it.storeType == type }
        } ?: partnerships

    val normalizedMenuSearchQuery: String
        get() = menuSearchQuery.trim()

    val isMenuSearchActive: Boolean
        get() = normalizedMenuSearchQuery.length >= MENU_SEARCH_MIN_LENGTH
}

data class FavoriteMenuSnackbarEvent(
    @StringRes val messageRes: Int,
    val isError: Boolean,
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val partnershipRepository: PartnershipRepository,
    private val favoritePartnershipDataStore: FavoritePartnershipDataStore,
    private val menuFavoriteRepository: MenuFavoriteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<FavoriteState>>(UiState.Init)
    val uiState: StateFlow<UiState<FavoriteState>> = _uiState.asStateFlow()

    private val _menuSnackbarEvents = MutableSharedFlow<FavoriteMenuSnackbarEvent>(
        extraBufferCapacity = 1,
    )
    val menuSnackbarEvents: SharedFlow<FavoriteMenuSnackbarEvent> =
        _menuSnackbarEvents.asSharedFlow()

    private var menuSearchJob: Job? = null

    fun loadFavorites() {
        viewModelScope.launch {
            val previous = (_uiState.value as? UiState.Success)?.data ?: FavoriteState()
            _uiState.value = UiState.Loading

            val partnershipJob = async {
                val items = partnershipRepository.getUserFavoritePartnerships()
                    .mapNotNull(Partnership::toFavoriteItemOrNull)
                    .distinctBy { it.partnershipId }
                val order = favoritePartnershipDataStore
                    .reconcile(items.map { it.partnershipId })
                    .distinct()
                val itemById = items.associateBy { it.partnershipId }
                order.mapNotNull(itemById::get)
            }
            val menuJob = async { menuFavoriteRepository.getFavoriteMenus() }

            val partnerships = partnershipJob.await()
            when (val menuResult = menuJob.await()) {
                is ApiResult.Success -> {
                    _uiState.value = UiState.Success(
                        previous.copy(
                            partnerships = partnerships,
                            favoriteMenus = menuResult.data,
                            favoriteMenuLoadState = MenuFavoriteLoadState.Success,
                        ),
                    )
                }

                else -> {
                    _uiState.value = UiState.Success(
                        previous.copy(
                            partnerships = partnerships,
                            favoriteMenus = emptyList(),
                            favoriteMenuLoadState = MenuFavoriteLoadState.Error,
                        ),
                    )
                    showMenuSnackbar(R.string.favorite_menu_load_error, isError = true)
                }
            }
        }
    }

    fun onMenuSearchQueryChanged(query: String) {
        menuSearchJob?.cancel()
        val keyword = query.trim()
        updateState {
            it.copy(
                menuSearchQuery = query,
                menuSearchResults = if (keyword.length < MENU_SEARCH_MIN_LENGTH) {
                    emptyList()
                } else {
                    it.menuSearchResults
                },
                menuSearchLoadState = if (keyword.length < MENU_SEARCH_MIN_LENGTH) {
                    MenuFavoriteLoadState.Idle
                } else {
                    MenuFavoriteLoadState.Loading
                },
            )
        }

        if (keyword.length < MENU_SEARCH_MIN_LENGTH) return

        menuSearchJob = viewModelScope.launch {
            when (val result = menuFavoriteRepository.searchMenus(keyword)) {
                is ApiResult.Success -> updateSearchResultIfCurrent(keyword) {
                    it.copy(
                        menuSearchResults = result.data,
                        menuSearchLoadState = MenuFavoriteLoadState.Success,
                    )
                }

                else -> {
                    updateSearchResultIfCurrent(keyword) {
                        it.copy(
                            menuSearchResults = emptyList(),
                            menuSearchLoadState = MenuFavoriteLoadState.Error,
                        )
                    }
                    showMenuSnackbar(R.string.favorite_menu_load_error, isError = true)
                }
            }
        }
    }

    fun toggleMenuFavorite(item: MenuFavoriteSearchResult) {
        viewModelScope.launch {
            val result = if (item.isFavorite) {
                menuFavoriteRepository.removeFavoriteMenu(item.menuId)
            } else {
                menuFavoriteRepository.addFavoriteMenu(item.menuId)
            }

            if (result is ApiResult.Success) {
                updateState { state ->
                    val updatedResults = state.menuSearchResults.map { resultItem ->
                        if (resultItem.menuId == item.menuId) {
                            resultItem.copy(isFavorite = !item.isFavorite)
                        } else {
                            resultItem
                        }
                    }
                    val updatedFavorites = if (item.isFavorite) {
                        state.favoriteMenus.filterNot { it.menuId == item.menuId }
                    } else {
                        listOf(
                            FavoriteMenu(
                                menuId = item.menuId,
                                menuName = item.menuName,
                                restaurant = item.restaurant,
                                isDiscontinued = false,
                            ),
                        ) + state.favoriteMenus.filterNot { it.menuId == item.menuId }
                    }
                    state.copy(
                        menuSearchResults = updatedResults,
                        favoriteMenus = updatedFavorites,
                    )
                }
                showMenuSnackbar(
                    messageRes = if (item.isFavorite) {
                        R.string.favorite_menu_deleted_snackbar
                    } else {
                        R.string.favorite_menu_added_snackbar
                    },
                    isError = false,
                )
            } else {
                showMenuSnackbar(R.string.favorite_menu_action_error, isError = true)
            }
        }
    }

    fun removeFavoriteMenu(item: FavoriteMenu) {
        viewModelScope.launch {
            when (menuFavoriteRepository.removeFavoriteMenu(item.menuId)) {
                is ApiResult.Success -> {
                    updateState { state ->
                        state.copy(
                            favoriteMenus = state.favoriteMenus.filterNot {
                                it.menuId == item.menuId
                            },
                            menuSearchResults = state.menuSearchResults.map { result ->
                                if (result.menuId == item.menuId) {
                                    result.copy(isFavorite = false)
                                } else {
                                    result
                                }
                            },
                        )
                    }
                    showMenuSnackbar(R.string.favorite_menu_deleted_snackbar, isError = false)
                }

                else -> showMenuSnackbar(R.string.favorite_menu_action_error, isError = true)
            }
        }
    }

    fun selectStoreType(storeType: StoreType?) {
        updateState { it.copy(selectedStoreType = storeType) }
    }

    fun removeFavorite(partnershipId: Int) {
        viewModelScope.launch {
            val result = partnershipRepository.likePartnership(partnershipId, wasLiked = true)
            if (result is ApiResult.Success) {
                updateState { state ->
                    state.copy(
                        partnerships = state.partnerships.filterNot {
                            it.partnershipId == partnershipId
                        },
                    )
                }
            }
        }
    }

    fun removeFavorites(partnershipIds: Set<Int>) {
        if (partnershipIds.isEmpty()) return

        viewModelScope.launch {
            val jobs = partnershipIds.map { id ->
                async {
                    val result = partnershipRepository.likePartnership(id, wasLiked = true)
                    if (result is ApiResult.Success) id else null
                }
            }
            val successfulIds = jobs.awaitAll().filterNotNull().toSet()
            if (successfulIds.isNotEmpty()) {
                updateState { state ->
                    state.copy(
                        partnerships = state.partnerships.filterNot {
                            it.partnershipId in successfulIds
                        },
                    )
                }
            }
        }
    }

    fun restoreFavorites(items: List<FavoritePartnershipItem>) {
        if (items.isEmpty()) return

        viewModelScope.launch {
            val jobs = items.map { item ->
                async {
                    val result = partnershipRepository.likePartnership(
                        item.partnershipId,
                        wasLiked = false,
                    )
                    if (result is ApiResult.Success) item else null
                }
            }
            val restoredItems = jobs.awaitAll().filterNotNull()
            if (restoredItems.isNotEmpty()) {
                updateState { state ->
                    val existingIds = state.partnerships.map { it.partnershipId }.toSet()
                    val newlyAdded = restoredItems.filterNot { it.partnershipId in existingIds }
                    state.copy(partnerships = newlyAdded + state.partnerships)
                }
            }
        }
    }

    private fun updateSearchResultIfCurrent(
        keyword: String,
        transform: (FavoriteState) -> FavoriteState,
    ) {
        updateState { state ->
            if (state.normalizedMenuSearchQuery == keyword) transform(state) else state
        }
    }

    private fun updateState(transform: (FavoriteState) -> FavoriteState) {
        _uiState.update { current ->
            if (current is UiState.Success) {
                UiState.Success(transform(current.data))
            } else {
                current
            }
        }
    }

    private fun showMenuSnackbar(@StringRes messageRes: Int, isError: Boolean) {
        _menuSnackbarEvents.tryEmit(
            FavoriteMenuSnackbarEvent(
                messageRes = messageRes,
                isError = isError,
            ),
        )
    }
}

private fun Partnership.toFavoriteItemOrNull(): FavoritePartnershipItem? {
    val representative = partnershipInfos.firstOrNull { it.isLiked }
        ?: partnershipInfos.firstOrNull()
        ?: return null

    return FavoritePartnershipItem(
        partnershipId = representative.id,
        storeName = storeName,
        storeType = restaurantType,
        description = representative.description,
        detail = PartnershipRestaurant(
            id = representative.id,
            partnershipType = representative.partnershipType,
            storeName = storeName,
            description = representative.description,
            startDate = representative.startDate,
            endDate = representative.endDate,
            storeType = restaurantType,
            longitude = longitude,
            latitude = latitude,
            collegeName = representative.collegeName,
            departmentName = representative.departmentName,
            partnershipLikeCount = representative.likeCount,
            likedByUser = representative.isLiked,
            naverMapUrl = naverMapUrl,
            kakaoMapUrl = kakaoMapUrl,
        ),
    )
}
