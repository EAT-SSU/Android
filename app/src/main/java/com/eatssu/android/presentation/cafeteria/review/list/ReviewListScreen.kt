package com.eatssu.android.presentation.cafeteria.review.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.util.showToast
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.component.ReviewItem
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Star
import timber.log.Timber

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = hiltViewModel(),
    menuType: MenuType,
    menuName: String,
    id: Long,
    onBack: () -> Unit = {},
    onWriteButtonClick: (menuName: String) -> Unit, // menuName을 인자로 받도록 수정
    onModifyClick: (Review) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = menuType, key2 = id) {
        viewModel.getReview(menuType, id)
    }

    val reviewListState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle(initialValue = null)

    when (uiEvent) {
        is UiEvent.ShowToast -> {
            context.showToast((uiEvent as UiEvent.ShowToast).message)
        }
    }

    ReviewListScreen(
        uiState = reviewListState,
        modifier = modifier,
        menuName = menuName,
        onBack = onBack,
        onReviewWriteButtonClick = onWriteButtonClick,
        onModifyClick = onModifyClick,
        onDeleteClick = { reviewId -> viewModel.deleteReview(reviewId) }
    )
}

@Composable
internal fun ReviewListScreen(
    uiState: UiState<ReviewListState>,
    modifier: Modifier = Modifier,
    menuName: String,
    onBack: () -> Unit = {},
    onReviewWriteButtonClick: (menuName: String) -> Unit,
    onModifyClick: (Review) -> Unit,
    onDeleteClick: (reviewId: Long) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedReviewId by remember { mutableStateOf<Long?>(null) }
    var selectedReview by remember { mutableStateOf<Review?>(null) }

    if (showBottomSheet && selectedReviewId != null) {
        MyReviewBottomSheet(
            onDismiss = { showBottomSheet = false; selectedReviewId = null },
            onModify = {
                selectedReview?.let { onModifyClick(it) }
                showBottomSheet = false
                selectedReviewId = null
                selectedReview = null
            },
            onDelete = {
                selectedReviewId?.let { onDeleteClick(it) }
                showBottomSheet = false
                selectedReviewId = null
                selectedReview = null
            }
        )
    }

    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = "리뷰",
                onBack = onBack
            )
        },
        bottomBar = { // 하단에 버튼을 고정하기 위함
            EatSsuButton(
                text = "리뷰 작성하기",
                onClick = {
                    // info.name을 전달 (메뉴명이 +로 합쳐진 값)
                    val menuName = (uiState as? UiState.Success)?.data?.reviewInfo?.name ?: ""
                    Timber.d("ReviewListScreen - info.name: '${(uiState as? UiState.Success)?.data?.reviewInfo?.name}', menuName: '$menuName'")
                    onReviewWriteButtonClick(menuName)
                },
                modifier = Modifier
                    .padding(24.dp)
            )
        },
    ) { innerPadding ->
        Surface(modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                when (uiState) {

                    is UiState.Init, UiState.Loading -> {
                        ReviewInfoContent(
                            ReviewInfo(
                                name = menuName,
                                reviewCnt = 0,
                                five = 0,
                                four = 0,
                                three = 0,
                                two = 0,
                                one = 0,
                                mainRating = 0.0,
                            )
                        )
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth()   // 가로 전체 차지
                                    .height(16.dp)
                                    .background(Gray100) // 배경색 적용
                            )

                            Row(Modifier.padding(horizontal = 24.dp)) {
                                Text(
                                    "리뷰",
                                    style = EatssuTheme.typography.h2,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "0",
                                    color = Primary,
                                    style = EatssuTheme.typography.h2,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }


                    is UiState.Success -> {
                        val info = uiState.data?.reviewInfo
                        val reviewList = uiState.data?.reviewList ?: emptyList()

                        ReviewInfoContent(info)

                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth()   // 가로 전체 차지
                                    .height(16.dp)
                                    .background(Gray100) // 배경색 적용
                            )

                            Row(Modifier.padding(horizontal = 24.dp)) {
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

                            if (uiState.data?.reviewList?.size == 0) {
                                EmptyReviewContent(
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else {
                                reviewList.forEach { item ->
                                    ReviewItem(
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                        isWriter = item.isWriter,
                                        writeName = item.writerNickname,
                                        writeDate = item.writeDate,
                                        content = item.content,
                                        rating = item.mainGrade,
                                        menuList = item.menuList,
                                        likeMenuList = item.likeMenuList,
                                        imgUrl = item.imgUrl,
                                        onMoreClick = {
                                            selectedReviewId = item.reviewId
                                            selectedReview = item
                                            showBottomSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    UiState.Error -> {
                        // TODO: 에러 UI
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewInfoContent(info: ReviewInfo?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
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
                    Icon(
                        painter = painterResource(R.drawable.ic_cafeteria_menu_selected),
                        modifier = Modifier.size(24.dp),
                        tint = Primary,
                        contentDescription = "map restaurant icon"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "오늘의 메뉴",
                        style = EatssuTheme.typography.subtitle1
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    info?.name.toString(),
                    textAlign = TextAlign.Center,
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = com.eatssu.design_system.R.drawable.ic_star_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = Star
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
                reviewCount = info?.reviewCnt ?: 0,
                fiveRatingCount = info?.five ?: 0,
                fourRatingCount = info?.four ?: 0,
                threeRatingCount = info?.three ?: 0,
                twoRatingCount = info?.two ?: 0,
                oneRatingCount = info?.one ?: 0,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}

@Composable
fun EmptyReviewContent(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(R.drawable.ic_none_review),
            "empty review",
            tint = Gray600,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "아직 작성된 리뷰가 없어요",
            style = EatssuTheme.typography.subtitle2,
            color = Gray600
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "메뉴에 가장 먼저 리뷰를 남겨주세요!",
            style = EatssuTheme.typography.caption2,
            color = Gray600
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기",
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
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
                            menuList = listOf("고구마치즈돈까스"),
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            mainGrade = 4,
                            content = "맛있어요",
                            likeMenuList = listOf("소고기"),
                            imgUrl = null,
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 1,
                            menuList = listOf("치킨가라아게"),
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
                            menuList = listOf("돈까스"),
                            writerNickname = "음식평론가",
                            writeDate = "2024-12-29",
                            mainGrade = 3,
                            content = "그럭저럭 괜찮아요",
                            imgUrl = null,
                            likeMenuList = null
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 2,
                            menuList = listOf("돈까스"),
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

@Preview(showBackground = true)
@Composable
fun ReviewListLoadingPreview() {
    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기",
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            uiState = UiState.Loading
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListEmptyPreview() {
    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기+닭고기+돼지고기+양고기",
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            uiState = UiState.Success(
                ReviewListState(
                    reviewInfo = ReviewInfo(
                        name = "소고기+닭고기+돼지고기+양고기+오리고기+닭고기+돼지고기+양고기",
                        reviewCnt = 0,
                        five = 0,
                        four = 0,
                        three = 0,
                        two = 0,
                        one = 0,
                        mainRating = 0.0,
                    ),
                    reviewList = emptyList()
                )
            ),
        )
    }
}