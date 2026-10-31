package com.eatssu.android.presentation.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary
import com.eatssu.design_system.theme.White
import com.eatssu.design_system.theme.pretendardMedium

@Composable
internal fun todayRestaurantLabel(restaurant: Restaurant): String = stringResource(
    when (restaurant) {
        Restaurant.HAKSIK -> R.string.today_favorite_haksik
        Restaurant.DODAM -> R.string.today_favorite_dodam
        Restaurant.DORMITORY -> R.string.today_favorite_dormitory
        else -> R.string.today_favorite_faculty
    },
)

@Composable
internal fun todayTimeLabel(time: Time): String = stringResource(
    when (time) {
        Time.MORNING -> R.string.widget_morning
        Time.LUNCH -> R.string.widget_lunch
        Time.DINNER -> R.string.widget_dinner
    },
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TodayMenuAccordion(
    state: TodayMenuFavoriteState,
    favoriteIds: Set<Long>,
    canSave: Boolean,
    onToggleExpanded: () -> Unit,
    onChangeMeal: () -> Unit,
    onToggleMenu: (Long) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, Gray300, shape)
            .padding(horizontal = 9.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val expansionDescription = stringResource(
            if (state.expanded) R.string.today_favorite_expanded else R.string.today_favorite_collapsed,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .semantics { stateDescription = expansionDescription }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onToggleExpanded,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(R.drawable.ic_today_menu_calendar),
                null,
                Modifier.size(20.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(4.dp))
            Text(
                stringResource(R.string.today_favorite_title),
                style = EatssuTheme.typography.caption1,
                color = Gray700
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painterResource(R.drawable.ic_today_menu_chevron),
                null,
                Modifier
                    .size(18.dp)
                    .rotate(if (state.expanded) 0f else 180f),
                tint = Color.Unspecified,
            )
        }
        if (state.expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.padding(horizontal = 2.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            todayRestaurantLabel(state.restaurant),
                            style = EatssuTheme.typography.subtitle2,
                            color = Gray700
                        )
                        Icon(
                            painterResource(R.drawable.ic_today_menu_dot),
                            null,
                            Modifier
                                .padding(horizontal = 6.dp)
                                .size(3.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            todayTimeLabel(state.time),
                            style = EatssuTheme.typography.subtitle2,
                            color = Gray700
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            stringResource(R.string.today_favorite_change),
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, Gray300, RoundedCornerShape(4.dp))
                                .clickable(enabled = !state.isSaving, onClick = onChangeMeal)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            style = EatssuTheme.typography.caption1, color = Gray600,
                        )
                    }
                    Text(
                        stringResource(R.string.today_favorite_hint),
                        style = EatssuTheme.typography.caption2,
                        color = Gray400
                    )
                }
                when (state.loadState) {
                    MenuFavoriteLoadState.Idle, MenuFavoriteLoadState.Loading ->
                        DelayedLoadingIndicator(Modifier
                            .fillMaxWidth()
                            .height(64.dp))

                    MenuFavoriteLoadState.Error -> Column(
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp
                        )
                    ) {
                        Text(
                            stringResource(R.string.favorite_menu_load_error),
                            style = EatssuTheme.typography.caption2,
                            color = Gray600
                        )
                        Text(
                            stringResource(R.string.button_retry),
                            Modifier.clickable(onClick = onRetry),
                            style = EatssuTheme.typography.caption1,
                            color = Primary,
                        )
                    }

                    MenuFavoriteLoadState.Success -> {
                        if (state.menus.isEmpty()) {
                            Text(
                                stringResource(R.string.today_favorite_no_menu),
                                style = EatssuTheme.typography.caption2,
                                color = Gray600
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                state.menus.forEach { menu ->
                                    val alreadyFavorite = menu.menuId in favoriteIds
                                    TodayMenuChip(
                                        label = menu.menuName,
                                        selected = alreadyFavorite || menu.menuId in state.selectedMenuIds,
                                        enabled = !alreadyFavorite && !state.isSaving,
                                        onClick = { onToggleMenu(menu.menuId) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
            val selectedCount = (state.selectedMenuIds - favoriteIds).size
            TodayMenuSaveButton(
                text = if (selectedCount == 0) stringResource(R.string.today_favorite_save)
                else stringResource(R.string.today_favorite_save_count, selectedCount),
                enabled = canSave && !state.isSaving && selectedCount > 0 && state.loadState == MenuFavoriteLoadState.Success,
                onClick = onSave,
            )
        }
    }
}

@Composable
private fun TodayMenuChip(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(1000.dp)
    Text(
        label,
        Modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clip(shape)
            .background(if (selected) Secondary else White)
            .border(1.dp, if (selected) Primary else Gray300, shape)
            .toggleable(
                value = selected,
                interactionSource = remember { MutableInteractionSource() }, indication = null,
                enabled = enabled, role = Role.Checkbox, onValueChange = { onClick() },
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        style = EatssuTheme.typography.caption3.copy(fontFamily = pretendardMedium),
        color = if (selected) Gray700 else Gray600,
    )
}

@Composable
private fun TodayMenuSaveButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(Primary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null,
                enabled = enabled, role = Role.Button, onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = EatssuTheme.typography.button2, color = White)
    }
}
