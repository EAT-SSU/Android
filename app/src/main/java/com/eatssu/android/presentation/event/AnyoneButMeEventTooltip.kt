package com.eatssu.android.presentation.event

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White

@Composable
fun AnyoneButMeEventTooltip(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EVENT!",
            modifier = Modifier
                .background(
                    color = Primary,
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = White,
            style = EatssuTheme.typography.caption2
        )

        Canvas(
            modifier = Modifier.size(width = 12.dp, height = 6.dp)
        ) {
            drawPath(
                path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width / 2f, size.height)
                    lineTo(size.width, 0f)
                    close()
                },
                color = Primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnyoneButMeEventTooltipPreview() {
    EatssuTheme {
        AnyoneButMeEventTooltip()
    }
}
