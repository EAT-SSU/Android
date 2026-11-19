package com.eatssu.android.presentation.map.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapStyleTabs(
    labels: List<String>,
    selectedPage: Int,
    onPageSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedPage,
        containerColor = White,
        contentColor = Primary,
        indicator = { positions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(positions[selectedPage]),
                color = Primary,
                height = 2.dp,
            )
        },
        divider = { HorizontalDivider(color = Gray300, thickness = 0.5.dp) },
    ) {
        labels.forEachIndexed { index, label ->
            Tab(
                selected = selectedPage == index,
                onClick = { onPageSelected(index) },
                text = {
                    Text(
                        text = label,
                        style = EatssuTheme.typography.subtitle2.copy(
                            fontWeight = if (selectedPage == index) FontWeight.Bold else FontWeight.Normal,
                        ),
                        color = if (selectedPage == index) Primary else Gray500,
                    )
                },
            )
        }
    }
}
