package com.eatssu.design_system.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary


@Composable
fun Chip(
    menuName: String,
    isLike: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(0.5.dp, Primary),
        color = Secondary,
        contentColor = Primary,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 5.dp) // 원하는 상하 여백 명시
                .heightIn(min = 16.dp), // 아이콘 유무와 상관없이 동일한 높이 유지 (16dp 아이콘 + 상하 5dp + 라운드 여유)
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLike) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thumb_up),
                    contentDescription = "thumb up Image",
                    modifier = Modifier.size(12.dp),
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = menuName,
                style = EatssuTheme.typography.caption3,
                color = Primary
            )
        }
    }
}

@ThemePreviews
@Composable
fun ChipPreview() {
    EatssuTheme {
        Column {
            Chip("고구마치즈돈까스", true)
            Spacer(modifier = Modifier.size(8.dp))
            Chip("고구마치즈돈까스", false)
        }
    }
}