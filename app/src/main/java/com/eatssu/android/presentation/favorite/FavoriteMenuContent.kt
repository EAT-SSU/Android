package com.eatssu.android.presentation.favorite

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eatssu.android.R
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Error
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.pretendardBold

@Composable
internal fun FavoriteMenuContent(
    uiState: UiState<FavoriteState>,
    onSearchQueryChanged: (String) -> Unit,
    onSearchFavoriteClick: (MenuFavoriteSearchResult) -> Unit,
    onFavoriteMenuClick: (FavoriteMenu) -> Unit,
    todayMenuState: TodayMenuFavoriteState,
    onTodayToggleExpanded: () -> Unit,
    onTodayChangeMeal: (Restaurant, Time) -> Unit,
    onTodayToggleMenu: (Long) -> Unit,
    onTodaySave: () -> Unit,
    onTodayRetry: () -> Unit,
    onSearchFocusChanged: (Boolean) -> Unit = {},
    onSearchSubmit: () -> Unit = {},
    onSearchExit: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val exitSearch = {
        focusManager.clearFocus(force = true)
        keyboard?.hide()
        onSearchExit()
    }
    BackHandler(enabled = (uiState as? UiState.Success)?.data?.isMenuSearchMode == true) { exitSearch() }
    var showMealSelection by rememberSaveable { mutableStateOf(false) }
    if (showMealSelection) {
        TodayMealSelectionSheet(
            restaurant = todayMenuState.restaurant,
            time = todayMenuState.time,
            onDismiss = { showMealSelection = false },
            onApply = { restaurant, time ->
                onTodayChangeMeal(restaurant, time)
                showMealSelection = false
            },
        )
    }
    when (uiState) {
        UiState.Init, UiState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        UiState.Error -> Unit
        is UiState.Success -> {
            val state = uiState.data
            val favoriteIds =
                state.favoriteMenus.map { it.menuId }.toSet() - state.unfavoritedMenuIds
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp,
                    bottom = 24.dp
                ),
            ) {
                item(key = "search") {
                    MenuFavoriteSearchField(
                        query = state.menuSearchQuery,
                        isSearchMode = state.isMenuSearchMode,
                        onQueryChanged = onSearchQueryChanged,
                        onFocusChanged = onSearchFocusChanged,
                        onSubmit = onSearchSubmit,
                        onExit = exitSearch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (!state.isMenuSearchMode) item(key = "today") {
                    Spacer(Modifier.height(20.dp))
                    TodayMenuAccordion(
                        state = todayMenuState,
                        favoriteIds = favoriteIds,
                        canSave = true,
                        onToggleExpanded = onTodayToggleExpanded,
                        onChangeMeal = { showMealSelection = true },
                        onToggleMenu = onTodayToggleMenu,
                        onSave = onTodaySave,
                        onRetry = onTodayRetry,
                    )
                }
                if (state.isMenuSearchMode) {
                    if (state.hasSubmittedMenuSearch) {
                        item { Spacer(Modifier.height(20.dp)) }
                        when (state.menuSearchLoadState) {
                            MenuFavoriteLoadState.Loading -> item {
                                DelayedLoadingIndicator(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )
                            }

                            MenuFavoriteLoadState.Success -> {
                                if (state.menuSearchResults.isEmpty()) item {
                                    MenuFavoriteEmptyState(
                                        stringResource(R.string.favorite_menu_search_empty_title),
                                        stringResource(R.string.favorite_menu_search_empty_description),
                                    )
                                }
                                items(
                                    state.menuSearchResults,
                                    key = { "search-${it.menuId}" }) { menu ->
                                    MenuSearchResultRow(
                                        menu,
                                        state.normalizedMenuSearchQuery
                                    ) { onSearchFavoriteClick(menu) }
                                    Spacer(Modifier.height(10.dp))
                                }
                            }

                            else -> Unit
                        }
                    }
                } else {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.favorite_menu_section_title),
                            style = EatssuTheme.typography.subtitle1,
                            color = Gray700
                        )
                        Spacer(Modifier.height(12.dp))
                        Spacer(Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Gray200))
                        Spacer(Modifier.height(12.dp))
                    }
                    if (state.favoriteMenuLoadState == MenuFavoriteLoadState.Loading) item {
                        DelayedLoadingIndicator(Modifier
                            .fillMaxWidth()
                            .height(120.dp))
                    } else if (state.favoriteMenus.isEmpty()) item {
                        MenuFavoriteEmptyState(
                            stringResource(R.string.favorite_menu_empty_title),
                            stringResource(R.string.favorite_menu_empty_description),
                        )
                    }
                    items(state.favoriteMenus, key = { "favorite-${it.menuId}" }) { menu ->
                        FavoriteMenuRow(
                            menu,
                            menu.menuId in favoriteIds
                        ) { onFavoriteMenuClick(menu) }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuFavoriteSearchField(
    query: String,
    isSearchMode: Boolean,
    onQueryChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(10.dp)

    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .onPreviewKeyEvent {
                if (it.key == Key.Enter || it.key == Key.NumPadEnter) {
                    if (it.type == KeyEventType.KeyUp) onSubmit()
                    true
                } else false
            }
            .height(50.dp)
            .clip(shape)
            .background(Gray100)
            .border(
                width = 1.dp,
                color = Gray200,
                shape = shape,
            ),
        textStyle = EatssuTheme.typography.body2.copy(color = Gray700),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        cursorBrush = SolidColor(Gray700),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(width = 15.28.dp, height = 14.dp),
                    tint = Gray400,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.favorite_menu_search_hint),
                            style = EatssuTheme.typography.body2,
                            color = Gray400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
                if (isSearchMode || query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(onClick = onExit),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(
                                R.string.favorite_menu_clear_search_content_description,
                            ),
                            modifier = Modifier.size(18.dp),
                            tint = Gray400,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun FavoriteMenuRow(
    menu: FavoriteMenu,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    MenuRow(
        text = menuLabel(
            menuName = menu.menuName,
            restaurant = stringResource(menu.restaurant.displayNameResId),
            isDiscontinued = menu.isDiscontinued,
        ),
        isFavorite = isFavorite,
        onFavoriteClick = onFavoriteClick,
    )
}

@Composable
private fun MenuSearchResultRow(
    menu: MenuFavoriteSearchResult,
    query: String,
    onFavoriteClick: () -> Unit,
) {
    MenuRow(
        text = menuLabel(
            menuName = menu.menuName,
            restaurant = stringResource(menu.restaurant.displayNameResId),
            query = query,
        ),
        isFavorite = menu.isFavorite,
        onFavoriteClick = onFavoriteClick,
    )
}

@Composable
private fun MenuRow(
    text: AnnotatedString,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = EatssuTheme.typography.body3,
            color = Gray700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.ic_like_filled else R.drawable.ic_like_line,
            ),
            contentDescription = stringResource(
                if (isFavorite) {
                    R.string.favorite_menu_remove_content_description
                } else {
                    R.string.favorite_menu_add_content_description
                },
            ),
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onFavoriteClick),
            tint = Gray700,
        )
    }
}

@Composable
private fun MenuFavoriteEmptyState(
    title: String,
    description: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = EatssuTheme.typography.subtitle1,
                color = Gray700,
            )
            Text(
                text = description,
                style = EatssuTheme.typography.caption2,
                color = Gray600,
            )
        }
    }
}

@Composable
private fun menuLabel(
    menuName: String,
    restaurant: String,
    query: String? = null,
    isDiscontinued: Boolean = false,
): AnnotatedString = buildAnnotatedString {
    append(menuName)

    if (!query.isNullOrBlank()) {
        val start = menuName.indexOf(query, ignoreCase = true)
        if (start >= 0) {
            addStyle(
                style = SpanStyle(
                    fontFamily = pretendardBold,
                    fontWeight = FontWeight.Bold,
                ),
                start = start,
                end = start + query.length,
            )
        }
    }

    append("  ·  ")
    val restaurantStart = length
    append(restaurant)
    addStyle(
        style = SpanStyle(
            color = Gray500,
            fontSize = 12.sp,
        ),
        start = restaurantStart - 5,
        end = length,
    )

    if (isDiscontinued) {
        append("  ")
        val discontinuedStart = length
        append(stringResource(R.string.favorite_menu_discontinued))
        addStyle(
            style = SpanStyle(
                color = Error,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            start = discontinuedStart,
            end = length,
        )
    }
}
