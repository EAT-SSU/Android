package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Star

@Composable
fun RatingBarSmall(
    rating: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start) {
        for (i in 1..5) {
            val tintColor = if (i <= rating) Star else Gray300
            Icon(
                painter = painterResource(id = R.drawable.ic_star_24),
                contentDescription = null,
                modifier = Modifier
                    .size(12.dp),
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
            IconButton(onClick = { onRatingChanged(i) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    tint = tintColor
                )
            }
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
    RatingBarSmall(rating = 3)
}