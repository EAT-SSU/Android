package com.eatssu.android.presentation.cafeteria.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.presentation.util.debouncedClickable
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun MenuItemRow(
    menu: Menu,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable(debounceInterval = 800L) { onClick() }
            .padding(start = 14.dp, end = 15.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = menu.name,
            style = EatssuTheme.typography.body3.copy(
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            color = Color.Black,
            modifier = Modifier.weight(7f),
        )
        Text(
            text = menu.price.toString(),
            style = EatssuTheme.typography.body3.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = when {
                menu.rate == 0.0 || menu.rate.isNaN() -> "-"
                else -> String.format("%.1f", menu.rate)
            },
            style = EatssuTheme.typography.body3.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun MenuItemRowPreview() {
    EatssuTheme {
        MenuItemRow(
            menu = Menu(
                id = 1L,
                name = "돈까스김치나베",
                price = 5500,
                rate = 4.3,
            ),
            onClick = {},
        )
    }
}
