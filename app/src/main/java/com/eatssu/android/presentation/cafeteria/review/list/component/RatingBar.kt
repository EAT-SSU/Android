package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Star

@Composable
fun RatingBar(
    isBig: Boolean,
    rating: Int = 1,
    onRatingChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start) {
        for (i in 1..maxRating) {
            val tintColor = if (i <= rating) Color(0xFFFFC107) else Color(0xFFDADADA) // 노랑 / 회색
            val iconSize = if (isBig) 24.dp else 12.dp

            Icon(
                painter = painterResource(id = R.drawable.ic_star_24),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onRatingChanged(i) },
                tint = tintColor
            )
        }
    }
}

@Composable
fun RatingBarMedium(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start) {
        for (i in 1..5) {
            val tintColor = if (i <= rating) Star else Gray300
            Icon(
                painter = painterResource(id = R.drawable.ic_star_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .clickable { onRatingChanged(i) },
                tint = tintColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarMediumPreview() {
    RatingBarMedium(rating = 3, onRatingChanged = {})
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    RatingBar(isBig = false, rating = 3, onRatingChanged = {})
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview2() {
    RatingBar(isBig = true, rating = 3, onRatingChanged = {}, modifier = Modifier, maxRating = 1)
}