package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White
import timber.log.Timber

enum class FilterType {
    All, Mine
}

@Composable
fun PartnershipFilterToggle(
    selected: FilterType,
    onSelectedChange: (FilterType) -> Unit,
    modifier: Modifier = Modifier,
    departmentName: String,
) {
    Timber.d("departmentName = $departmentName")
    Row(
        modifier = modifier
            .border(1.dp, Gray300, shape = CircleShape)
            .clip(CircleShape)
            .background(White)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PartnershipToggleItem(
            text = if (departmentName == "학과" || departmentName.isEmpty()) "내 제휴" else departmentName,
            isSelected = selected == FilterType.Mine,
            onClick = { onSelectedChange(FilterType.Mine) },
        )
        PartnershipToggleItem(
            text = "전체",
            isSelected = selected == FilterType.All,
            onClick = { onSelectedChange(FilterType.All) }
        )
    }
}

@Composable
fun PartnershipToggleItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Primary else Color.Transparent
    val textColor = if (isSelected) Color.White else Gray600

    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = EatssuTheme.typography.body2
        )
    }
}