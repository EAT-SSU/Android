package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray400

@Composable
fun ReviewItem(
    modifier: Modifier,
    writeName: String,
    writeDate: String,
    content: String,
) {

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_24),
                contentDescription = "Profile Image",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    writeName,
                    style = EatssuTheme.typography.caption1
                )
                RatingBar(isBig = false, rating = 3, onRatingChanged = {})
            }

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_three_dot),
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified,
                )
                Text(
                    writeDate,
                    style = EatssuTheme.typography.caption3,
                    color = Gray400,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Tag(menuName = "고구마치즈돈까스", modifier = Modifier) //todo tag 변환
        Spacer(modifier = Modifier.height(8.dp))

        Text(content, style = EatssuTheme.typography.body3)
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewItemPreview() {

    ReviewItem(
        modifier = Modifier,
        writeName = "숭실푸드파이터",
        writeDate = "2024-12-31",
        content = "맛있어요"
    )
}