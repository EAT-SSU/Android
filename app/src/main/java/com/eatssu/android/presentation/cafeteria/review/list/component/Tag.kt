package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Primary

//@Composable
//fun Tag(
//    menuName: String,
//    modifier: Modifier = Modifier
//) {
//    FilterChip(
//        onClick = {},
//        leadingIcon = {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_thumb_up),
//                contentDescription = "thumb up Image",
//                modifier = Modifier.size(20.dp),
//                tint = Color.Unspecified,
//            )
//        },
//        label = {
//            Text(
//                text = menuName,
//                style = EatssuTheme.typography.caption3,
//                color = Primary
//            )
//        },
//        selected = true,
//        modifier = modifier,
//        shape = RoundedCornerShape(30.dp),
//        colors = FilterChipDefaults.filterChipColors(
//            selectedContainerColor = MaterialTheme.colorScheme.secondary,
//            containerColor = MaterialTheme.colorScheme.surface,
//            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
//            labelColor = MaterialTheme.colorScheme.secondary,
//        ),
//        border = FilterChipDefaults.filterChipBorder(
//            enabled = true,
//            selected = false,
//            borderColor = Primary,
//            borderWidth = 0.5.dp
//        ),
//
//    )
//}

@Composable
fun Tag(
    menuName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(0.5.dp, Primary),
        color = MaterialTheme.colorScheme.surface,
        contentColor = Primary,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 5.dp), // 원하는 상하 여백 명시
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_thumb_up),
                contentDescription = "thumb up Image",
                modifier = Modifier.size(16.dp), // 아이콘 크기도 조절
                tint = Primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = menuName,
                style = EatssuTheme.typography.caption3,
                color = Primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagPreview() {
    com.eatssu.android.presentation.compose.ui.theme.EatssuTheme {
        Tag("고구마치즈돈까스")
    }
}