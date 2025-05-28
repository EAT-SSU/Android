package com.eatssu.android.presentation.cafeteria.review.list

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
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
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.list.component.RatingBar
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.cafeteria.review.list.component.Tag
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = hiltViewModel()
) {

    val reviewListState by viewModel.uiState.collectAsStateWithLifecycle()

    InternalReviewListScreen(
        uiState = reviewListState,
        modifier = modifier,
    )
}

@Composable
internal fun InternalReviewListScreen(
    uiState: UiState<ReviewListState>,
    modifier: Modifier = Modifier,
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

            when (uiState) {
                is UiState.Success -> {
                    val info = uiState.data?.reviewInfo
                    val reviewList = uiState.data?.reviewList ?: emptyList()

                    Column {
                        Text(info?.name.toString())

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RatingBar(rating = 1, onRatingChanged = {}, maxRating = 1)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(info?.mainRating.toString())
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_thumb_up),
                                        contentDescription = "thumb up icon",
                                        modifier = Modifier.size(28.dp),
                                        tint = Color.Unspecified
                                    )
                                    Text(info?.reviewCnt.toString()) // TODO 좋아요

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_thumb_down),
                                        contentDescription = "thumb down icon",
                                        modifier = Modifier.size(28.dp),
                                        tint = Color.Unspecified
                                    )
                                    Text(info?.reviewCnt.toString()) // TODO 싫어요
                                }
                            }

                            ReviewProgressBar(
                                reviewCount = info?.reviewCnt ?: 0,
                                fiveRatingCount = info?.five ?: 0,
                                fourRatingCount = info?.four ?: 0,
                                threeRatingCount = info?.three ?: 0,
                                twoRatingCount = info?.two ?: 0,
                                oneRatingCount = info?.one ?: 0
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("리뷰")

                        LazyColumn {
                            items(reviewList) { item ->
                                ReviewItem(
                                    modifier = Modifier,
                                    writeName = item.writerNickname,
                                    writeDate = item.writeDate,
                                    content = item.content
                                )
                            }
                        }
                    }
                }

                UiState.Loading -> {
                    // TODO: 로딩 UI
                }

                UiState.Error -> {
                    // TODO: 에러 UI
                }

                UiState.Init -> {
                    // TODO: 초기 상태 UI
                }
            }



        }
    }
}


@Composable
fun ReviewItem(
    modifier: Modifier,
    writeName: String,
    writeDate: String,
    content: String,
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
                Text(writeName)
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
                Text(writeDate)
            }
        }
        Tag(menuName = "고구마치즈돈까스", modifier = Modifier) //todo tag 변환

        Text(content)
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        InternalReviewListScreen(
            uiState = UiState.Success(
                ReviewListState(
                    isEmpty = false,
                    reviewInfo = ReviewInfo(
                        name = "고기",
                        reviewCnt = 123,
                        five = 80,
                        four = 20,
                        three = 10,
                        two = 5,
                        one = 8,
                        mainRating = 4.5,
                        amountRating = 2.3,
                        tasteRating = 4.5
                    ),
                    reviewList = listOf(
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menu = "고구마치즈돈까스",
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            amountGrade = 2,
                            tasteGrade = 4,
                            content = "맛있어요",
                            imgUrl = null
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menu = "고구마치즈돈까스",
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            amountGrade = 2,
                            tasteGrade = 4,
                            content = "맛있어요",
                            imgUrl = null
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menu = "고구마치즈돈까스",
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            amountGrade = 2,
                            tasteGrade = 4,
                            content = "맛있어요",
                            imgUrl = null
                        )
                    )
                )
            ),
        )
    }
}