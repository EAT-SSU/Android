package com.eatssu.android.presentation.cafeteria.review.write.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.component.LikeButton
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun MenuLikeButtonItem(
    modifier: Modifier,
    mealName: String,
    isLiked: Boolean,
    onLikeChanged: (Boolean) -> Unit,
) {

    Row(modifier.padding(vertical = 6.dp)) {
        Text(
            mealName,
            style = EatssuTheme.typography.body3
        )
        Spacer(modifier = Modifier.weight(1f))
        LikeButton(
            isLiked = isLiked,
            onClick = {
                onLikeChanged(!isLiked) // 클릭 시 상태를 반전
            }
        )
    }
}

@ThemePreviews
@Composable
private fun MenuLikeButtonItemPreview() {
    EatssuTheme {
        MenuLikeButtonItem(
            modifier = Modifier,
            mealName = "제육볶음",
            isLiked = true,
            onLikeChanged = {},
        )
    }
}
