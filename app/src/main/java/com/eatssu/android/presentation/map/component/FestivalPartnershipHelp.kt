package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.White
import com.eatssu.design_system.theme.pretendardMedium

private val FestivalWine = Color(0xFF880A19)

@Composable
internal fun FestivalPartnershipHelp(
    isMessageVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isMessageVisible) {
            Box(modifier = Modifier.width(223.dp)) {
                Image(
                    painter = painterResource(R.drawable.bg_festival_partnership_help),
                    contentDescription = null,
                    modifier = Modifier.size(width = 222.dp, height = 48.dp),
                )

                Column(
                    modifier = Modifier
                        .padding(start = 13.dp, top = 6.dp),
                ) {
                    FestivalHelpText(text = stringResource(R.string.festival_partnership_help_title))
                    FestivalHelpText(text = stringResource(R.string.festival_partnership_help_warning))
                }
            }
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .background(FestivalWine.copy(alpha = 0.9f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_festival_partnership_info),
                contentDescription = stringResource(R.string.festival_partnership_help_content_description),
                modifier = Modifier.size(14.533.dp),
            )
        }
    }
}

@Composable
private fun FestivalHelpText(text: String) {
    Text(
        text = text,
        color = White,
        maxLines = 1,
        style = EatssuTheme.typography.caption2.copy(
            fontFamily = pretendardMedium,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun FestivalPartnershipHelpPreview() {
    EatssuTheme {
        FestivalPartnershipHelp(
            isMessageVisible = true,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
