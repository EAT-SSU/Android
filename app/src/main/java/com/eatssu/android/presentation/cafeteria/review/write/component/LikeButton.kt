package com.eatssu.android.presentation.cafeteria.review.write.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White

@Composable
fun LikeButton(
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isLiked) Primary else White
    val contentColor = if (isLiked) White else Gray300
    val borderColor = if (isLiked) Primary else Gray300

    Button(
        onClick = onClick,
        modifier = modifier.size(58.dp, 28.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp)
    ) {
        Icon(
            painter = painterResource(id = com.eatssu.android.R.drawable.ic_thumb_up), // R.drawable 경로가 올바른지 확인
            contentDescription = "like",
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview
@Composable
fun LikeButtonPreview() {
    EatssuTheme {
        Column {
            LikeButton(isLiked = true, onClick = {})
            Spacer(Modifier.size(8.dp))
            LikeButton(isLiked = false, onClick = {})
        }
    }

}