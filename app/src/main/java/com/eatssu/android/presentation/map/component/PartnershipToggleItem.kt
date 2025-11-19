package com.eatssu.android.presentation.map.component

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
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White
import timber.log.Timber

enum class FilterType {
    Mine, All; // 해당 부분에 쓰는 enum 순서대로 UI 토글 순서에 반영됩니다

    fun label(departmentName: String): String {
        return when (this) {
            Mine -> if (departmentName.isBlank() || departmentName == "학과") "내 제휴" else departmentName
            All -> "전체"
        }
    }
}

@Composable
fun PartnershipFilterToggle(
    selected: FilterType,
    onSelectedChange: (FilterType) -> Unit,
    modifier: Modifier = Modifier,
    departmentName: String,
) {
    Timber.d("departmentName = $departmentName")
    val items = FilterType.entries.map {
        it to it.label(departmentName)
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
            ToggleChip(
                label = label,
                selected = selected == type,
                onClick = { onSelectedChange(type) }
            )
        }
    }
}

@Composable
fun ToggleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) Primary else Color.Transparent
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