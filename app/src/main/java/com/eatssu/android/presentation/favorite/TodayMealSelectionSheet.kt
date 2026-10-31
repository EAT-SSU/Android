package com.eatssu.android.presentation.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodayMealSelectionSheet(
    restaurant: Restaurant,
    time: Time,
    onApply: (Restaurant, Time) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRestaurant by rememberSaveable { mutableStateOf<Restaurant?>(restaurant) }
    var selectedTime by rememberSaveable { mutableStateOf<Time?>(time) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = White,
        scrimColor = Black.copy(alpha = 0.15f),
        dragHandle = null,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 14.dp, bottom = 10.dp),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(30.dp)
                    .height(2.dp)
                    .background(Gray400, RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.height(14.dp))
            Text(
                stringResource(R.string.today_favorite_sheet_title),
                Modifier.align(Alignment.CenterHorizontally),
                style = EatssuTheme.typography.subtitle1,
                color = Black
            )
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.today_favorite_restaurant_title),
                style = EatssuTheme.typography.subtitle2,
                color = Black
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                todayMenuRestaurants.forEach { option ->
                    MealSelectionChip(
                        todayRestaurantLabel(option),
                        selectedRestaurant == option,
                        true,
                        Modifier.weight(1f)
                    ) {
                        selectedRestaurant = option
                        if (!isTodayMealAvailable(option, selectedTime)) selectedTime = null
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.today_favorite_time_title),
                style = EatssuTheme.typography.subtitle2,
                color = Black
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Time.entries.forEach { option ->
                    MealSelectionChip(
                        todayTimeLabel(option), selectedTime == option,
                        isTodayMealAvailable(selectedRestaurant, option), Modifier.weight(1f),
                    ) { selectedTime = option }
                }
            }
            Spacer(Modifier.height(50.dp))
            EatSsuButton(
                text = stringResource(R.string.today_favorite_apply),
                enabled = isTodayMealAvailable(selectedRestaurant, selectedTime),
                onClick = {
                    val chosenRestaurant = selectedRestaurant
                    val chosenTime = selectedTime
                    if (chosenRestaurant != null && chosenTime != null && isTodayMealAvailable(
                            chosenRestaurant,
                            chosenTime
                        )
                    ) {
                        onApply(chosenRestaurant, chosenTime)
                    }
                },
            )
        }
    }
}

@Composable
private fun MealSelectionChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier
            .height(36.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(shape)
            .background(if (selected) Primary else White)
            .border(if (selected) 0.dp else 1.dp, if (selected) Primary else Gray300, shape)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = if (selected) EatssuTheme.typography.body2 else EatssuTheme.typography.body3,
            color = if (selected) White else Gray400,
            maxLines = 1,
            softWrap = false,
        )
    }
}
