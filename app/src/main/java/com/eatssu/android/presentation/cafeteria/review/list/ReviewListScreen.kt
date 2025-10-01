package com.eatssu.android.presentation.cafeteria.review.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewItem
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.cafeteria.review.report.ReportActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Star

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = hiltViewModel(),
    menuType: MenuType,
    menuName: String,
    id: Long,
    onBack: () -> Unit = {},
    onWriteButtonClick: () -> Unit,
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
    onReviewWriteButtonClick: () -> Unit,
    onModifyClick: (Review) -> Unit,
    onDeleteClick: (reviewId: Long) -> Unit,
) {
    val context = LocalContext.current

    var showMyBottomSheet by remember { mutableStateOf(false) }
    var showOthersBottomSheet by remember { mutableStateOf(false) }

    var selectedReview by remember { mutableStateOf<Review?>(null) }

    if (showOthersBottomSheet && selectedReview != null) {
        OthersReviewBottomSheet(
            onDismiss = { showOthersBottomSheet = false; selectedReview = null },
            onReport = {
                val intent = android.content.Intent(context, ReportActivity::class.java)
                intent.putExtra("reviewId", selectedReview?.reviewId)
                context.startActivity(intent)
                showOthersBottomSheet = false
                selectedReview = null
            }
        )
    }

    if (showMyBottomSheet && selectedReview != null) {
        MyReviewBottomSheet(
            onDismiss = { showMyBottomSheet = false; selectedReview = null },
            onModify = {
                selectedReview?.let { onModifyClick(it) }
                showMyBottomSheet = false
                selectedReview = null
            },
            onDelete = {
                selectedReview?.let { onDeleteClick(it.reviewId) }
                showMyBottomSheet = false
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
                    onReviewWriteButtonClick()
                },
                modifier = Modifier
                    .padding(24.dp)
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                when (uiState) {

                    is UiState.Init, UiState.Loading -> {
                        ReviewInfoContent(
                            menuName, ReviewInfo(
                                reviewCnt = 0,
                                five = 0,
                                four = 0,
                                three = 0,
                                two = 0,
                                one = 0,
                                rating = 0.0,
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
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxHeight()
                                    .padding(top = 100.dp)
                            ) {
                                DelayedLoadingIndicator(modifier = Modifier)
                            }
                        }
                    }


                    is UiState.Success -> {
                        val info = uiState.data?.reviewInfo
                        val reviewList = uiState.data?.reviewList ?: emptyList()

                        ReviewInfoContent(menuName, info)

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
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(top = 100.dp),
                                )
                            } else {
                                reviewList.forEach { item ->
                                    ReviewItem(
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                        isWriter = item.isWriter,
                                        writeName = item.writerNickname,
                                        writeDate = item.writeDate,
                                        content = item.content,
                                        rating = item.rating,
                                        menuList = item.menuList,
                                        imgUrl = item.imgUrl,
                                        onMoreClick = {
                                            if (item.isWriter) {
                                                showMyBottomSheet = true
                                                selectedReview = item
                                            } else {
                                                showOthersBottomSheet = true
                                                selectedReview = item
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    UiState.Error -> {
                        // TODO: 에러 UI
                        ReviewInfoContent(
                            menuName,
                            ReviewInfo(
                                reviewCnt = 0,
                                five = 0,
                                four = 0,
                                three = 0,
                                two = 0,
                                one = 0,
                                rating = 0.0,
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
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxHeight()
                                    .padding(top = 100.dp)
                            ) {
                                Text(
                                    "에러가 발생했습니다.",
                                    style = EatssuTheme.typography.body1,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewInfoContent(
    menuName: String,
    info: ReviewInfo?
) {
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
                    menuName,
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
                    if (info?.reviewCnt == 0) "-" else info?.rating.toString(),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = EatssuTheme.typography.rate
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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
                        reviewCnt = 123,
                        five = 80,
                        four = 20,
                        three = 10,
                        two = 5,
                        one = 8,
                        rating = 4.5,
                    ),
                    reviewList = listOf(
                        Review(
                            isWriter = false,
                            reviewId = 0,
                            menuList = listOf(
                                Review.Menu(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.Menu(
                                    menuId = 2L,
                                    name = "닭고기",
                                    isLike = false
                                )
                            ),
                            writerNickname = "숭실푸드파이터",
                            writeDate = "2024-12-31",
                            rating = 4,
                            content = "맛있어요",
                            imgUrl = null,
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 1,
                            menuList = listOf(
                                Review.Menu(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.Menu(
                                    menuId = 2L,
                                    name = "닭고기",
                                    isLike = false
                                )
                            ),
                            writerNickname = "맛있는리뷰어",
                            writeDate = "2024-12-30",
                            rating = 5,
                            content = "정말 맛있어요! 다음에도 먹고 싶어요.",
                            imgUrl = null,
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 2,
                            menuList = listOf(
                                Review.Menu(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.Menu(
                                    menuId = 2L,
                                    name = "닭고기",
                                    isLike = false
                                )
                            ),
                            writerNickname = "음식평론가",
                            writeDate = "2024-12-29",
                            rating = 3,
                            content = "그럭저럭 괜찮아요",
                            imgUrl = null,
                        ),
                        Review(
                            isWriter = false,
                            reviewId = 2,
                            menuList = listOf(
                                Review.Menu(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.Menu(
                                    menuId = 2L,
                                    name = "닭고기",
                                    isLike = false
                                )
                            ),
                            writerNickname = "음식평론가",
                            writeDate = "2024-12-29",
                            rating = 3,
                            content = "그럭저럭 괜찮아요",
                            imgUrl = "https://picsum.photos/400/301", // 실제 이미지 URL 사용
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
                        reviewCnt = 0,
                        five = 0,
                        four = 0,
                        three = 0,
                        two = 0,
                        one = 0,
                        rating = 0.0,
                    ),
                    reviewList = emptyList()
                )
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListErrorPreview() {
    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기",
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            uiState = UiState.Error
        )
    }
}