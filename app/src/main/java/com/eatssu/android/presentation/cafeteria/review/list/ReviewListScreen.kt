package com.eatssu.android.presentation.cafeteria.review.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.eatssu.android.presentation.cafeteria.review.list.component.EatssuButton
import com.eatssu.android.presentation.cafeteria.review.list.component.RatingBar
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewItem
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
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
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp), // 버튼 영역만큼 아래 패딩,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("리뷰")

                when (uiState) {
                    is UiState.Success -> {
                        val info = uiState.data?.reviewInfo
                        val reviewList = uiState.data?.reviewList ?: emptyList()

                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                info?.name.toString(),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                style = EatssuTheme.typography.h2
                            )

                            Spacer(modifier = Modifier.height(13.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        RatingBar(
                                            isBig = true,
                                            rating = 1,
                                            onRatingChanged = {},
                                            maxRating = 1
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            info?.mainRating.toString(),
                                            modifier = Modifier.align(Alignment.CenterVertically),
                                            style = EatssuTheme.typography.rate
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_thumb_up),
                                            contentDescription = "thumb up icon",
                                            modifier = Modifier.size(28.dp),
                                            tint = Color.Unspecified
                                        )
                                        Text(
                                            info?.reviewCnt.toString(),
                                            style = EatssuTheme.typography.subtitle2
                                        ) // TODO 좋아요

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_thumb_down),
                                            contentDescription = "thumb down icon",
                                            modifier = Modifier.size(28.dp),
                                            tint = Color.Unspecified
                                        )
                                        Text(
                                            info?.reviewCnt.toString(),
                                            style = EatssuTheme.typography.subtitle2
                                        ) // TODO 싫어요
                                    }
                                }

                                Spacer(modifier = Modifier.width(37.dp))

                                ReviewProgressBar(
                                    reviewCount = info?.reviewCnt ?: 0,
                                    fiveRatingCount = info?.five ?: 0,
                                    fourRatingCount = info?.four ?: 0,
                                    threeRatingCount = info?.three ?: 0,
                                    twoRatingCount = info?.two ?: 0,
                                    oneRatingCount = info?.one ?: 0,
                                    modifier = Modifier.width(150.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "리뷰",
                                style = EatssuTheme.typography.h2,
                            )

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


            // 하단 고정 버튼
            EatssuButton(
                "리뷰 작성하기",
                {},
                modifier.align(Alignment.BottomCenter),
            )
        }
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
                        name = "소고기+닭고기+돼지고기+양고기+오리고기",
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