package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme

@Composable
fun ReviewProgressBar(
    modifier: Modifier = Modifier,
    reviewCount: Int = 0,
    fiveRatingCount: Int = 0,
    fourRatingCount: Int = 0,
    threeRatingCount: Int = 0,
    twoRatingCount: Int = 0,
    oneRatingCount: Int = 0,
) {
    val ratingList = listOf(
        5 to fiveRatingCount,
        4 to fourRatingCount,
        3 to threeRatingCount,
        2 to twoRatingCount,
        1 to oneRatingCount
    )

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.total_review_num),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$reviewCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ratingList.forEach { (rating, count) ->
            val percent = if (reviewCount > 0) count / reviewCount.toFloat() else 0f

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(
                        id = when (rating) {
                            5 -> R.string.rate_5
                            4 -> R.string.rate_4
                            3 -> R.string.rate_3
                            2 -> R.string.rate_2
                            else -> R.string.rate_1
                        }
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { percent.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewProgressBarPreview() {
    EatssuTheme {
        ReviewProgressBar(
            reviewCount = 100,
            fiveRatingCount = 60,
            fourRatingCount = 20,
            threeRatingCount = 10,
            twoRatingCount = 7,
            oneRatingCount = 3
        )
    }
}