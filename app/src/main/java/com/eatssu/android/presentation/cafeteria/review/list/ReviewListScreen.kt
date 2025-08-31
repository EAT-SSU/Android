package com.eatssu.android.presentation.cafeteria.review.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.list.component.EatssuButton
import com.eatssu.android.presentation.cafeteria.review.list.component.RatingBar
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewItem
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray100
import com.eatssu.android.presentation.compose.ui.theme.Primary

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = hiltViewModel(),
    menuType: MenuType,
    id: Long,
) {
    LaunchedEffect(key1 = menuType, key2 = id) {
        // 2. key가 변경되면 이 블록이 다시 실행됨
        // 뷰모델의 함수를 호출하여 데이터를 로드
        viewModel.loadReview(menuType, id)
    }

    val reviewListState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewListScreen(
        uiState = reviewListState,
        modifier = modifier,
    )
}

@Composable
internal fun ReviewListScreen(
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)) // 모서리 곡률을 16.dp로 둥글게 만듭니다.
                                    .background(Gray100) // 배경색 설정
                                    .padding(horizontal = 16.dp, vertical = 13.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row {
                                        Image(
                                            painter = painterResource(R.drawable.ic_map_restaurant),
                                            "map restaurant icon"
                                        )
                                        Text(
                                            "오늘의 메뉴",
                                            style = EatssuTheme.typography.subtitle1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        info?.name.toString(),
                                        modifier = Modifier,
                                        style = EatssuTheme.typography.body1
                                    )
                                }

                            }

                            Box(
                                modifier = Modifier
                                    .height(12.dp)
                                    .background(Gray100)
                                    .padding(vertical = 16.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 36.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
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


                                Spacer(modifier = Modifier.width(37.dp))

                                ReviewProgressBar(
                                    fiveRatingCount = info?.five ?: 0,
                                    fourRatingCount = info?.four ?: 0,
                                    threeRatingCount = info?.three ?: 0,
                                    twoRatingCount = info?.two ?: 0,
                                    oneRatingCount = info?.one ?: 0,
                                    modifier = Modifier.width(150.dp)
                                )
                            }


                            Row {

                                Text(
                                    "리뷰",
                                    style = EatssuTheme.typography.h2,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${info?.reviewCnt}",
                                    color = Primary,
                                    style = EatssuTheme.typography.h2,
                                )
                            }

                            if (uiState.data?.reviewInfo?.reviewCnt == 0) {


                            } else {

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
        ReviewListScreen(
            uiState = UiState.Success(
                ReviewListState(
                    reviewInfo = ReviewInfo(
                        name = "소고기+닭고기+돼지고기+양고기+오리고기",
                        reviewCnt = 123,
                        five = 80,
                        four = 20,
                        three = 10,
                        two = 5,
                        one = 8,
                        mainRating = 4.5,
//                        likeCount = 100,
//                        unlikeCount = 50,
                    ),
                    reviewList = listOf(
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menu = "고구마치즈돈까스",
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            content = "맛있어요",
                            likeMenuList = listOf("소고기"),
                            imgUrl = null
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menu = "고구마치즈돈까스",
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            content = "맛있어요",
                            imgUrl = null,
                            likeMenuList = listOf("소고기"),

                            ),
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menu = "고구마치즈돈까스",
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            content = "맛있어요",
                            imgUrl = null,
                            likeMenuList = null,
                        )
                    )
                )
            ),
        )
    }
}