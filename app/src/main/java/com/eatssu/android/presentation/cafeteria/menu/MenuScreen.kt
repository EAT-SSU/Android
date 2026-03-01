package com.eatssu.android.presentation.cafeteria.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.model.Section
import com.eatssu.android.presentation.cafeteria.info.InfoBottomSheet
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.eatssu.design_system.preview.ThemePreviews
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel,
    infoViewModel: InfoViewModel,
    time: Time,
    selectedDate: LocalDate,
    onNavigateToReview: (menuType: MenuType, itemId: Long, itemName: String) -> Unit,
) {
    val uiState by menuViewModel.uiState.collectAsStateWithLifecycle()
    var showInfoSheet by remember { mutableStateOf(false) }
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }

    var sectionList by remember { mutableStateOf<List<Section>>(emptyList()) }

    LaunchedEffect(selectedDate, time) {
        val menuDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val dayOfWeek = selectedDate.dayOfWeek

        val restaurantsToLoad = buildList {
            addAll(Restaurant.getVariableRestaurantList())
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && time == Time.LUNCH) {
                add(Restaurant.FOOD_COURT)
                add(Restaurant.SNACK_CORNER)
            }
        }

        menuViewModel.loadMenus(restaurantsToLoad, menuDate, time)
    }

    LaunchedEffect(uiState) {
        val successState = uiState as? MenuUiState.Success ?: return@LaunchedEffect
        val menuMap = successState.menuMap
        sectionList = buildList {
            menuMap
                .filter { (_, menuList) -> menuList.isNotEmpty() }
                .forEach { (restaurant, menuList) ->
                    val location =
                        infoViewModel.getRestaurantInfo(restaurant)?.location ?: ""
                    add(
                        Section(
                            restaurant.menuType,
                            restaurant,
                            menuList,
                            location,
                        )
                    )
                }
        }.sortedBy { it.cafeteria.ordinal }
    }

    if (showInfoSheet && selectedRestaurant != null) {
        InfoBottomSheet(
            restaurant = selectedRestaurant!!,
            infoViewModel = infoViewModel,
            onDismiss = { showInfoSheet = false },
        )
    }

    MenuScreenContent(
        uiState = uiState,
        sectionList = sectionList,
        onInfoClick = { section ->
            selectedRestaurant = section.cafeteria
            showInfoSheet = true
            EventLogger.clickRestaurantInfo(section.cafeteria)
        },
        onMenuClick = { section, menu ->
            onNavigateToReview(
                section.cafeteria.menuType,
                menu.id,
                menu.name,
            )
            EventLogger.clickMenu(section.cafeteria)
        },
    )
}

@Composable
internal fun MenuScreenContent(
    uiState: MenuUiState,
    sectionList: List<Section>,
    onInfoClick: (Section) -> Unit,
    onMenuClick: (Section, Menu) -> Unit,
) {

    when (uiState) {
        MenuUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                DelayedLoadingIndicator()
            }
        }

        is MenuUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Gray100)
                    .padding(bottom = 60.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(
                    items = sectionList,
                    key = { "section_${it.cafeteria.name}" },
                ) { section ->
                    CafeteriaSectionItem(
                        section = section,
                        onInfoClick = { onInfoClick(section) },
                        onMenuClick = { menu ->
                            onMenuClick(section, menu)
                        },
                    )
                }
            }
        }

        else -> Unit
    }
}

@ThemePreviews
@Composable
private fun MenuScreenContentPreview() {
    val sampleSections = listOf(
        Section(
            menuType = MenuType.VARIABLE,
            cafeteria = Restaurant.HAKSIK,
            menuList = listOf(
                Menu(id = 1L, name = "치킨마요덮밥", price = 5500, rate = 4.3),
                Menu(id = 2L, name = "돈까스", price = 6000, rate = 4.1),
            ),
            cafeteriaLocation = "학생회관 1층",
        ),
        Section(
            menuType = MenuType.VARIABLE,
            cafeteria = Restaurant.DORMITORY,
            menuList = listOf(
                Menu(id = 3L, name = "제육볶음", price = 6500, rate = 4.2),
            ),
            cafeteriaLocation = "기숙사 식당",
        ),
    )

    EatssuTheme {
        MenuScreenContent(
            uiState = MenuUiState.Success(),
            sectionList = sampleSections,
            onInfoClick = {},
            onMenuClick = { _, _ -> },
        )
    }
}
@ThemePreviews
@Composable
private fun MenuScreenContentLoadingPreview() {
    EatssuTheme {
        MenuScreenContent(
            uiState = MenuUiState.Loading,
            sectionList = emptyList(),
            onInfoClick = {},
            onMenuClick = { _, _ -> },
        )
    }
}

@ThemePreviews
@Composable
private fun MenuScreenContentErrorPreview() {
    EatssuTheme {
        MenuScreenContent(
            uiState = MenuUiState.Loading,
            sectionList = emptyList(),
            onInfoClick = {},
            onMenuClick = { _, _ -> },
        )
    }
}
