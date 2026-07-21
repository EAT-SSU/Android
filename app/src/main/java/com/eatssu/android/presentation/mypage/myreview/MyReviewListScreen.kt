package com.eatssu.android.presentation.mypage.myreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.list.component.MyReviewBottomSheet
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewItem
import com.eatssu.android.presentation.cafeteria.review.translation.ReviewTranslationUiState
import com.eatssu.android.presentation.cafeteria.review.translation.currentReviewTranslationTargetLanguage
import com.eatssu.android.presentation.cafeteria.review.translation.shouldShowReviewTranslationAction
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray600
import timber.log.Timber

@Composable
fun MyReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: MyReviewViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onModifyClick: (Review) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getMyReviewList()
        viewModel.loadUserNickname()
    }

    val reviewListState by viewModel.uiState.collectAsStateWithLifecycle()
    val userNickname by viewModel.nickname.collectAsStateWithLifecycle()
    val translationStates by viewModel.translationStates.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle(initialValue = null)
    val targetLanguage = currentReviewTranslationTargetLanguage()

    when (uiEvent) {
        is UiEvent.ShowToast -> {
            context.showToast(uiEvent as UiEvent.ShowToast)
        }
    }

    MyReviewListScreen(
        uiState = reviewListState,
        userNickname = userNickname,
        translationStates = translationStates,
        modifier = modifier,
        onBack = onBack,
        onDeleteClick = { reviewId -> viewModel.deleteReview(reviewId) },
        onModifyClick = onModifyClick,
        onTranslationClick = { review ->
            targetLanguage?.let { viewModel.toggleReviewTranslation(review, it) }
        },
        targetLanguage = targetLanguage,
        isLoggedIn = viewModel.isLoggedIn,
    )
}

@Composable
internal fun MyReviewListScreen(
    uiState: UiState<MyReviewState>,
    userNickname: String,
    translationStates: Map<Long, ReviewTranslationUiState>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onModifyClick: (Review) -> Unit,
    onDeleteClick: (reviewId: Long) -> Unit,
    onTranslationClick: (Review) -> Unit,
    targetLanguage: String?,
    isLoggedIn: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<Review?>(null) }
    val reviewListScrollState = rememberLazyListState()

    if (showBottomSheet && selectedReview != null) {
        MyReviewBottomSheet(
            onDismiss = { showBottomSheet = false; selectedReview = null },
            onModify = {
                selectedReview?.let { onModifyClick(it) }
                showBottomSheet = false
                selectedReview = null
            },
            onDelete = {
                selectedReview?.let { onDeleteClick(it.reviewId) }
                showBottomSheet = false
                selectedReview = null
            }
        )
    }

    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.my_review),
                onBack = onBack
            )
        },
    ) { innerPadding ->
        Surface(modifier = modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                when (uiState) {
                    is UiState.Success -> {
                        when (val dataState = uiState.data) {
                            is MyReviewState.ReviewExists -> {
                                Timber.d("리뷰 존재")
                                val reviewList = dataState.myReviews ?: emptyList()

                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 24.dp),
                                    state = reviewListScrollState,
                                ) {
                                    items(reviewList) { item ->
                                        val translationState = translationStates[item.reviewId]
                                        ReviewItem(
                                            modifier = Modifier,
                                            writeName = userNickname,
                                            writeDate = item.writeDate,
                                            content = item.content,
                                            rating = item.rating,
                                            menuLikeInfoList = item.menuLikeInfoList,
                                            imgUrl = item.imgUrl,
                                            translatedContent = translationState?.translatedContent,
                                            isTranslationVisible = translationState?.isTranslated == true,
                                            isTranslationLoading = translationState?.isLoading == true,
                                            isTranslationUnavailable = translationState?.isUnavailable == true,
                                            isParentScrolling = reviewListScrollState.isScrollInProgress,
                                            showTranslationAction = shouldShowReviewTranslationAction(
                                                targetLanguage = targetLanguage,
                                                isLoggedIn = isLoggedIn,
                                                content = item.content,
                                            ),
                                            onTranslationClick = { onTranslationClick(item) },
                                            onMoreClick = {
                                                selectedReview = item
                                                showBottomSheet = true
                                            }
                                        )
                                    }
                                }
                            }

                            is MyReviewState.NoReview -> {
                                Timber.d("리뷰 없음")
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Gray100),
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
                                        stringResource(R.string.none_review),
                                        style = EatssuTheme.typography.subtitle2,
                                        color = Gray600
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.none_review_my),
                                        style = EatssuTheme.typography.caption2,
                                        color = Gray600
                                    )
                                }
                            }
                        }
                    }


                    UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
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
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        MyReviewListScreen(
            userNickname = "숭실푸드파이터",
            onDeleteClick = {},
            onModifyClick = {},
            onTranslationClick = {},
            targetLanguage = "EN",
            isLoggedIn = true,
            translationStates = emptyMap(),
            uiState = UiState.Success(
                MyReviewState.ReviewExists(
                    myReviews = listOf(
                        Review(
                            isWriter = true,
                            reviewId = 0,
                            menuLikeInfoList = listOf(
                                Review.MenuLikeInfo(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.MenuLikeInfo(
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
                            isWriter = true,
                            reviewId = 1,
                            menuLikeInfoList = listOf(
                                Review.MenuLikeInfo(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.MenuLikeInfo(
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
                            isWriter = true,
                            reviewId = 2,
                            menuLikeInfoList = listOf(
                                Review.MenuLikeInfo(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.MenuLikeInfo(
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
                            menuLikeInfoList = listOf(
                                Review.MenuLikeInfo(
                                    menuId = 1L,
                                    name = "소고기",
                                    isLike = true
                                ), Review.MenuLikeInfo(
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
fun ReviewListEmptyPreview() {
    EatssuTheme {
        MyReviewListScreen(
            userNickname = "숭실푸드파이터",
            onDeleteClick = {},
            onModifyClick = {},
            onTranslationClick = {},
            targetLanguage = "EN",
            isLoggedIn = true,
            translationStates = emptyMap(),
            uiState = UiState.Success(
                MyReviewState.NoReview
            )
        )
    }
}
