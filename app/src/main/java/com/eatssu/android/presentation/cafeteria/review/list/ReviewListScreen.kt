package com.eatssu.android.presentation.cafeteria.review.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.list.component.RatingBar
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.cafeteria.review.list.component.Tag
import com.eatssu.android.presentation.cafeteria.review.write.ReviewWriteViewModel
import com.eatssu.android.presentation.cafeteria.review.write.WriteReviewState
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewWriteViewModel = hiltViewModel()
) {

    val learningListState by viewModel.uiState.collectAsStateWithLifecycle()


    InternalReviewListScreen(
        uiState = learningListState,
        modifier = modifier,
        menuName = "고기",
        rating = 4.5f,
        reviewCount = 10
    )
}

@Composable
internal fun InternalReviewListScreen(
    uiState: UiState<WriteReviewState>,
    modifier: Modifier = Modifier,
    menuName: String,
    rating: Float,
    reviewCount: Int,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
//        color = EatssuTheme.colors.background
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
        ) {
            Text("리뷰")

            Text(menuName)

            Row {
                Column {
                    Text(rating.toString())
                    Row {
                        Text("굿")
                        Text("12")
                        Text("배드")
                        Text("3")
                    }
                }

                Row {
                    ReviewProgressBar(reviewCount, 5, 4, 0, 0, 1)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "리뷰",
//                textAlign = TextAlign.Start
            )

            LazyColumn {
                items(3) { index ->
                    ReviewItem(modifier = Modifier, index)
                }
            }
        }
    }
}


@Composable
fun ReviewItem(
    modifier: Modifier,
    index: Int
) {

    Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)) {
        Row(modifier = modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_24),
                contentDescription = "Profile Image",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text("숭실푸드파이터 $index")
                RatingBar(3, {})
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
                Text("2024-12-31")
            }
        }
        Tag(menuName = "고구마치즈돈까스", modifier = Modifier)

        Text("맛있어요")
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        InternalReviewListScreen(
            uiState = UiState.Success(),
            menuName = "고기",
            rating = 4.5f,
            reviewCount = 10
        )
    }
}