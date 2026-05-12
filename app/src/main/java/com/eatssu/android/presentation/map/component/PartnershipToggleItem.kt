package com.eatssu.android.presentation.map.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Festival
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White
import timber.log.Timber

enum class FilterType(@StringRes val labelResId: Int) {
    Festival(R.string.partnership_filter_festival),
    All(R.string.partnership_filter_all),
    Mine(R.string.partnership_filter_mine),
}

@Composable
fun FilterType.getLabel(departmentName: String): String {
    val placeholderDepartment = stringResource(R.string.partnership_filter_department_placeholder)
    return when (this) {
        FilterType.Mine -> {
            if (departmentName.isBlank() || departmentName == placeholderDepartment) {
                stringResource(labelResId)
            } else {
                departmentName
            }
        }
        FilterType.Festival -> stringResource(labelResId)
        FilterType.All -> stringResource(labelResId)
    }
}

@Composable
fun PartnershipFilterToggle(
    selected: FilterType,
    onSelectedChange: (FilterType) -> Unit,
    departmentName: String,
    modifier: Modifier = Modifier,
) {
    Timber.d("departmentName = $departmentName")
    val items = FilterType.entries.map {
        it to it.getLabel(departmentName)
    }
    Row(
        modifier = modifier
            .border(1.dp, Gray300, CircleShape)
            .clip(CircleShape)
            .background(White)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (type, label) ->
            PartnershipToggleItem(
                label = label,
                selected = selected == type,
                onClick = { onSelectedChange(type) }
            )
        }
    }
}

@Composable
fun PartnershipToggleItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (selected && (label == stringResource(R.string.partnership_filter_festival)))
            Festival
        else if (selected)
            Primary
        else
            Color.Transparent
    val textColor = if (selected) White else Gray600

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = EatssuTheme.typography.body2
        )
    }
}