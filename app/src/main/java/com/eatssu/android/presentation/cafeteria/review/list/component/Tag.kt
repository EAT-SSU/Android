package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eatssu.android.R

@Composable
fun Tag(
    menuName: String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = {},
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_thumb_up),
                contentDescription = "thumb up Image",
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified,
            )
        },
        label = {
            Text(
                text = menuName,
                modifier = Modifier.graphicsLayer {
                    translationY = (-1.5).dp.toPx()
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        selected = true,
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surface,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
            labelColor = MaterialTheme.colorScheme.secondary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = true,
            borderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 0.5.dp
        ),
    )
}
