package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200

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

        ratingList.forEach { (rating, count) ->
            val percent = if (reviewCount > 0) (count.toFloat() / reviewCount.toFloat()) else 0f

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
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
                    style = EatssuTheme.typography.caption2,
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) { // LinearProgressIndicator의 구현 자체가 progress와 track 중간에 여백이 있어서
                    // 이를 커버하기 위해 Box로 감싸서 두개를 겹쳐놓음
                    // 첫번째 LinearProgressIndicator는 그레이색 배경만
                    // 두번째 LinearProgressIndicator가 진행
                    LinearProgressIndicator(
                        progress = { 0f },
                        modifier = Modifier
                            .height(5.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Gray200,
                    )
                    LinearProgressIndicator(
                        progress = { percent.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .matchParentSize()
                            .height(5.dp),
                        color = MaterialTheme.colorScheme.primary,
                        drawStopIndicator = {},
                        trackColor = Gray200,
                    )

                }
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

@Preview(showBackground = true)
@Composable
fun ReviewProgressBar1Preview() {
    EatssuTheme {

        ReviewProgressBar(
            reviewCount = 100,
            fiveRatingCount = 100,
            fourRatingCount = 0,
            threeRatingCount = 0,
            twoRatingCount = 0,
            oneRatingCount = 0
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewProgressBarEmptyPreview() {
    EatssuTheme {
        ReviewProgressBar(
            reviewCount = 0,
            fiveRatingCount = 0,
            fourRatingCount = 0,
            threeRatingCount = 0,
            twoRatingCount = 0,
            oneRatingCount = 0
        )
    }
}