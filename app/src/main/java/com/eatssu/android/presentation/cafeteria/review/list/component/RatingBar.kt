package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.eatssu.android.R

@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start) {
        for (i in 1..maxRating) {
            val tintColor = if (i <= rating) Color(0xFFFFC107) else Color(0xFFDADADA) // 노랑 / 회색

            Icon(
                painter = painterResource(id = R.drawable.ic_star_24),
                contentDescription = null,
                modifier = Modifier
//                    .size(24.dp)
                    .clickable { onRatingChanged(i) },
                tint = tintColor
            )
        }
    }
}