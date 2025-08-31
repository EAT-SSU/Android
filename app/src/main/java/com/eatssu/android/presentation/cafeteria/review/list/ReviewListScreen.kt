package com.eatssu.android.presentation.cafeteria.review.list

import EatSsuButton
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
import com.eatssu.android.presentation.cafeteria.review.list.component.RatingBar
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewItem
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray100
import com.eatssu.android.presentation.compose.ui.theme.Primary
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = hiltViewModel(),
    menuType: MenuType,
    id: Long,
    onReviewWriteButtonClick: (menuName: String) -> Unit // menuName을 인자로 받도록 수정
) {

    LaunchedEffect(key1 = menuType, key2 = id) {
        viewModel.loadReview(menuType, id)
    }

    val reviewListState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewListScreen(
        uiState = reviewListState,
        modifier = modifier,
        onReviewWriteButtonClick = onReviewWriteButtonClick
    )
}

@Composable
internal fun ReviewListScreen(
    uiState: UiState<ReviewListState>,
    modifier: Modifier = Modifier,
    onReviewWriteButtonClick: (menuName: String) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("리뷰")

                when (uiState) {
                    is UiState.Success -> {
                        val info = uiState.data?.reviewInfo
                        val reviewList = uiState.data?.reviewList ?: emptyList()

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .padding(24.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Gray100)
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
                                    .padding(horizontal = 24.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RatingBar(
                                        isBig = true,
                                        rating = info?.mainRating?.roundToInt() ?: 0,
                                        onRatingChanged = {},
                                        maxRating = 1
                                    ).also {
                                        Timber.d("ReviewListScreen - mainRating: ${info?.mainRating}, rounded: ${info?.mainRating?.roundToInt()}")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        info?.mainRating.toString(),
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        style = EatssuTheme.typography.rate
                                    )
                                }


                                Spacer(modifier = Modifier.width(37.dp))

                                ReviewProgressBar(
                                    reviewCount = info?.reviewCnt ?: 0,
                                    fiveRatingCount = info?.five ?: 0,
                                    fourRatingCount = info?.four ?: 0,
                                    threeRatingCount = info?.three ?: 0,
                                    twoRatingCount = info?.two ?: 0,
                                    oneRatingCount = info?.one ?: 0,
                                    modifier = Modifier.fillMaxWidth()
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
                                //todo 텅처리
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(reviewList) { item ->
                                        ReviewItem(
                                            modifier = Modifier,
                                            writeName = item.writerNickname,
                                            writeDate = item.writeDate,
                                            content = item.content,
                                            rating = item.mainGrade,
                                            likeMenuList = item.likeMenuList,
                                            imgUrl = item.imgUrl?.toString(),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    UiState.Loading -> {
                        // TODO: 로딩 UI
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    UiState.Error -> {
                        // TODO: 에러 UI
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    UiState.Init -> {
                        // TODO: 초기 상태 UI
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 하단 고정 버튼
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                EatSsuButton(
                    "리뷰 작성하기",
                    onClick = {
                        // info.name을 전달 (메뉴명이 +로 합쳐진 값)
                        val menuName = (uiState as? UiState.Success)?.data?.reviewInfo?.name ?: ""
                        Timber.d("ReviewListScreen - info.name: '${(uiState as? UiState.Success)?.data?.reviewInfo?.name}', menuName: '$menuName'")
                        onReviewWriteButtonClick(menuName)
                    },
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        ReviewListScreen(
            onReviewWriteButtonClick = {},
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
                            imgUrl = "https://picsum.photos/400/300" // 실제 이미지 URL 사용
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 1,
                            menu = "치킨가라아게",
                            writerNickname = "맛있는리뷰어",
                            writeDate = "2024-12-30",
                            mainGrade = 5,
                            content = "정말 맛있어요! 다음에도 먹고 싶어요.",
                            imgUrl = null,
                            likeMenuList = listOf("치킨가라아게", "감자튀김")
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 2,
                            menu = "돈까스",
                            writerNickname = "음식평론가",
                            writeDate = "2024-12-29",
                            mainGrade = 3,
                            content = "그럭저럭 괜찮아요",
                            imgUrl = "https://picsum.photos/400/301", // 실제 이미지 URL 사용
                            likeMenuList = null
                        )
                    )
                )
            ),
        )
    }
}