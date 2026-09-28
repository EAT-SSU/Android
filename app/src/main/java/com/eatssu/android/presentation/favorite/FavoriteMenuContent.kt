package com.eatssu.android.presentation.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eatssu.android.R
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.common.UiState
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
import java.util.Locale

@Composable
internal fun FavoriteMenuContent(
    uiState: UiState<FavoriteState>,
    onSearchQueryChanged: (String) -> Unit,
    onSearchFavoriteClick: (MenuFavoriteSearchResult) -> Unit,
    onFavoriteMenuClick: (FavoriteMenu) -> Unit,
) {
    when (uiState) {
        UiState.Init, UiState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        UiState.Error -> Unit
        is UiState.Success -> {
            val state = uiState.data
            Column(modifier = Modifier.fillMaxSize()) {
                MenuFavoriteSearchField(
                    query = state.menuSearchQuery,
                    onQueryChanged = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp),
                )

                if (state.isMenuSearchActive) {
                    MenuSearchContent(
                        state = state,
                        onFavoriteClick = onSearchFavoriteClick,
                    )
                } else {
                    FavoriteMenuListContent(
                        state = state,
                        onFavoriteClick = onFavoriteMenuClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuFavoriteSearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(10.dp)

    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
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
            }
        },
    )
}

@Composable
private fun FavoriteMenuListContent(
    state: FavoriteState,
    onFavoriteClick: (FavoriteMenu) -> Unit,
) {
    when (state.favoriteMenuLoadState) {
        MenuFavoriteLoadState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        MenuFavoriteLoadState.Error,
        MenuFavoriteLoadState.Idle,
        MenuFavoriteLoadState.Success,
        -> if (state.favoriteMenus.isEmpty()) {
            MenuFavoriteEmptyState(
                title = stringResource(R.string.favorite_menu_empty_title),
                description = stringResource(R.string.favorite_menu_empty_description),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(horizontal = 2.dp)
                    .padding(top = 22.dp),
            ) {
                Text(
                    text = stringResource(R.string.favorite_menu_section_title),
                    style = EatssuTheme.typography.subtitle1,
                    color = Gray700,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gray200),
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = state.favoriteMenus,
                        key = FavoriteMenu::menuId,
                    ) { menu ->
                        FavoriteMenuRow(
                            menu = menu,
                            onFavoriteClick = { onFavoriteClick(menu) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSearchContent(
    state: FavoriteState,
    onFavoriteClick: (MenuFavoriteSearchResult) -> Unit,
) {
    when (state.menuSearchLoadState) {
        MenuFavoriteLoadState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        MenuFavoriteLoadState.Success -> if (state.menuSearchResults.isEmpty()) {
            MenuFavoriteEmptyState(
                title = stringResource(R.string.favorite_menu_search_empty_title),
                description = stringResource(R.string.favorite_menu_search_empty_description),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    items = state.menuSearchResults,
                    key = MenuFavoriteSearchResult::menuId,
                ) { menu ->
                    MenuSearchResultRow(
                        menu = menu,
                        query = state.normalizedMenuSearchQuery,
                        onFavoriteClick = { onFavoriteClick(menu) },
                    )
                }
            }
        }

        MenuFavoriteLoadState.Error,
        MenuFavoriteLoadState.Idle,
        -> Unit
    }
}

@Composable
private fun FavoriteMenuRow(
    menu: FavoriteMenu,
    onFavoriteClick: () -> Unit,
) {
    MenuRow(
        text = menuLabel(
            menuName = menu.menuName,
            restaurant = stringResource(menu.restaurant.displayNameResId),
            isDiscontinued = menu.isDiscontinued,
        ),
        isFavorite = true,
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
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.offset(y = (-65).dp),
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
        val start = menuName.lowercase(Locale.getDefault())
            .indexOf(query.lowercase(Locale.getDefault()))
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
