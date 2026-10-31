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
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.StoreType
import com.eatssu.common.enums.Time
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
import java.time.LocalDate
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
    val unfavoritedMenuIds: Set<Long> = emptySet(),
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

    private val _todayMenuState = MutableStateFlow(TodayMenuFavoriteState())
    val todayMenuState: StateFlow<TodayMenuFavoriteState> = _todayMenuState.asStateFlow()
    private var todayMenuLoadJob: Job? = null
    private var todayMenuLoadVersion = 0
    private var hasConfiguredTodayMenus = false

    fun configureTodayMenus(restaurant: Restaurant?, time: Time?, expanded: Boolean) {
        if (hasConfiguredTodayMenus) return
        hasConfiguredTodayMenus = true
        val initialRestaurant =
            restaurant?.takeIf { it in todayMenuRestaurants } ?: Restaurant.HAKSIK
        val initialTime = time?.takeIf { isTodayMealAvailable(initialRestaurant, it) } ?: Time.LUNCH
        _todayMenuState.value = TodayMenuFavoriteState(
            restaurant = initialRestaurant, time = initialTime, expanded = expanded,
        )
        if (expanded) loadTodayMenus()
    }

    fun toggleTodayMenus() {
        _todayMenuState.update { it.copy(expanded = !it.expanded) }
        if (_todayMenuState.value.expanded) {
            refreshTodayMenusIfDateChanged()
            if (_todayMenuState.value.loadState == MenuFavoriteLoadState.Idle) loadTodayMenus()
        }
    }

    fun refreshTodayMenusIfDateChanged() {
        if (_todayMenuState.value.date == LocalDate.now()) return
        todayMenuLoadJob?.cancel()
        _todayMenuState.update {
            it.copy(
                date = LocalDate.now(),
                menus = emptyList(),
                selectedMenuIds = emptySet(),
                loadState = MenuFavoriteLoadState.Idle
            )
        }
        if (_todayMenuState.value.expanded) loadTodayMenus()
    }

    fun changeTodayMeal(restaurant: Restaurant, time: Time) {
        if (!isTodayMealAvailable(restaurant, time) || _todayMenuState.value.isSaving) return
        if (_todayMenuState.value.restaurant == restaurant && _todayMenuState.value.time == time) return
        _todayMenuState.update {
            it.copy(
                restaurant = restaurant,
                time = time,
                menus = emptyList(),
                selectedMenuIds = emptySet()
            )
        }
        loadTodayMenus()
    }

    fun loadTodayMenus() {
        todayMenuLoadJob?.cancel()
        val requestVersion = ++todayMenuLoadVersion
        _todayMenuState.update {
            it.copy(
                date = LocalDate.now(),
                loadState = MenuFavoriteLoadState.Loading,
                selectedMenuIds = emptySet()
            )
        }
        val request = _todayMenuState.value
        todayMenuLoadJob = viewModelScope.launch {
            val result =
                menuFavoriteRepository.getTodayMenus(request.date, request.restaurant, request.time)
            _todayMenuState.update { current ->
                if (requestVersion != todayMenuLoadVersion || current.restaurant != request.restaurant || current.time != request.time || current.date != request.date) current
                else when (result) {
                    is ApiResult.Success -> current.copy(
                        menus = result.data,
                        loadState = MenuFavoriteLoadState.Success
                    )

                    else -> current.copy(
                        menus = emptyList(),
                        loadState = MenuFavoriteLoadState.Error
                    )
                }
            }
        }
    }

    fun toggleTodayMenu(menuId: Long) {
        val state = _todayMenuState.value
        if (state.isSaving || state.menus.none { it.menuId == menuId } || menuId in favoriteMenuIds()) return
        _todayMenuState.update {
            it.copy(selectedMenuIds = if (menuId in it.selectedMenuIds) it.selectedMenuIds - menuId else it.selectedMenuIds + menuId)
        }
    }

    fun saveTodayMenus() {
        val state = _todayMenuState.value
        if (state.isSaving || _uiState.value !is UiState.Success) return
        if (state.date != LocalDate.now()) {
            refreshTodayMenusIfDateChanged(); return
        }
        val menus =
            state.menus.filter { it.menuId in state.selectedMenuIds && it.menuId !in favoriteMenuIds() }
        if (menus.isEmpty() || menus.any { it.menuId in pendingMenuActions }) return
        pendingMenuActions.addAll(menus.map { it.menuId })
        _todayMenuState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val results = menus.map { menu ->
                    async { menu to menuFavoriteRepository.addFavoriteMenu(menu.menuId) }
                }.awaitAll()
                val added = results.filter { it.second is ApiResult.Success }.map { it.first }
                val ids = added.map { it.menuId }.toSet()
                updateState { current ->
                    current.copy(
                        favoriteMenus = added.map {
                            FavoriteMenu(
                                it.menuId,
                                it.menuName,
                                it.restaurant,
                                false
                            )
                        } +
                                current.favoriteMenus.filterNot { it.menuId in ids },
                        unfavoritedMenuIds = current.unfavoritedMenuIds - ids,
                        menuSearchResults = current.menuSearchResults.map {
                            if (it.menuId in ids) it.copy(
                                isFavorite = true
                            ) else it
                        },
                    )
                }
                _todayMenuState.update { it.copy(selectedMenuIds = it.selectedMenuIds - ids) }
                showMenuSnackbar(
                    if (added.size == menus.size) R.string.favorite_menu_added_snackbar else R.string.favorite_menu_action_error,
                    isError = added.size != menus.size,
                )
            } finally {
                pendingMenuActions.removeAll(menus.map { it.menuId }.toSet())
                _todayMenuState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun favoriteMenuIds(): Set<Long> {
        val state = (_uiState.value as? UiState.Success)?.data ?: return emptySet()
        return state.favoriteMenus.map { it.menuId }.toSet() - state.unfavoritedMenuIds
    }

    private var menuSearchJob: Job? = null
    private val pendingMenuActions = mutableSetOf<Long>()

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
                            unfavoritedMenuIds = emptySet(),
                            favoriteMenuLoadState = MenuFavoriteLoadState.Success,
                        ),
                    )
                }

                else -> {
                    _uiState.value = UiState.Success(
                        previous.copy(
                            partnerships = partnerships,
                            favoriteMenus = emptyList(),
                            unfavoritedMenuIds = emptySet(),
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
        if (!pendingMenuActions.add(item.menuId)) return
        viewModelScope.launch {
            try {
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
                        val updatedFavorites = if (
                            item.isFavorite || state.favoriteMenus.any { it.menuId == item.menuId }
                        ) {
                            state.favoriteMenus
                        } else {
                            listOf(
                                FavoriteMenu(
                                    menuId = item.menuId,
                                    menuName = item.menuName,
                                    restaurant = item.restaurant,
                                    isDiscontinued = false,
                                ),
                            ) + state.favoriteMenus
                        }
                        state.copy(
                            menuSearchResults = updatedResults,
                            favoriteMenus = updatedFavorites,
                            unfavoritedMenuIds = if (item.isFavorite) {
                                state.unfavoritedMenuIds + item.menuId
                            } else {
                                state.unfavoritedMenuIds - item.menuId
                            },
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
            } finally {
                pendingMenuActions.remove(item.menuId)
            }
        }
    }

    fun toggleFavoriteMenu(item: FavoriteMenu) {
        if (!pendingMenuActions.add(item.menuId)) return
        viewModelScope.launch {
            try {
                val state = (_uiState.value as? UiState.Success)?.data ?: return@launch
                val isFavorite = item.menuId !in state.unfavoritedMenuIds
                val result = if (isFavorite) {
                    menuFavoriteRepository.removeFavoriteMenu(item.menuId)
                } else {
                    menuFavoriteRepository.addFavoriteMenu(item.menuId)
                }

                when (result) {
                    is ApiResult.Success -> {
                        updateState { current ->
                            current.copy(
                                unfavoritedMenuIds = if (isFavorite) {
                                    current.unfavoritedMenuIds + item.menuId
                                } else {
                                    current.unfavoritedMenuIds - item.menuId
                                },
                                menuSearchResults = current.menuSearchResults.map { searchResult ->
                                    if (searchResult.menuId == item.menuId) {
                                        searchResult.copy(isFavorite = !isFavorite)
                                    } else {
                                        searchResult
                                    }
                                },
                            )
                        }
                        showMenuSnackbar(
                            if (isFavorite) R.string.favorite_menu_deleted_snackbar
                            else R.string.favorite_menu_added_snackbar,
                            isError = false,
                        )
                    }

                    else -> showMenuSnackbar(R.string.favorite_menu_action_error, isError = true)
                }
            } finally {
                pendingMenuActions.remove(item.menuId)
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
