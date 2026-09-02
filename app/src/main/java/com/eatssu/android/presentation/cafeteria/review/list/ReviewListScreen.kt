package com.eatssu.android.presentation.cafeteria.review.list

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.eatssu.android.R
import com.eatssu.android.analytics.LocalAnalyticsTracker
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.presentation.cafeteria.review.list.component.MyReviewBottomSheet
import com.eatssu.android.presentation.cafeteria.review.list.component.OthersReviewBottomSheet
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewItem
import com.eatssu.android.presentation.cafeteria.review.list.component.ReviewProgressBar
import com.eatssu.android.presentation.cafeteria.review.report.ReportActivity
import com.eatssu.android.presentation.cafeteria.review.translation.ReviewTranslationUiState
import com.eatssu.android.presentation.cafeteria.review.translation.currentReviewTranslationTargetLanguage
import com.eatssu.android.presentation.cafeteria.review.translation.shouldShowReviewTranslationAction
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.analytics.ReviewAnalyticsEvent
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Star
import kotlinx.coroutines.flow.flowOf

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = hiltViewModel(),
    menuType: MenuType,
    restaurant: Restaurant,
    menuName: String,
    id: Long,
    refreshNonce: Long = 0L,
    onBack: () -> Unit = {},
    onWriteButtonClick: () -> Unit,
    onModifyClick: (Review) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = menuType, key2 = id, key3 = menuName) {
        viewModel.getReview(menuType, id, menuName)
    }

    // Screen View 로깅
    TrackScreenViewEvent(ScreenId.REVIEW_V2_VIEW)

    val reviewListState by viewModel.uiState.collectAsStateWithLifecycle()
    val translationStates by viewModel.translationStates.collectAsStateWithLifecycle()
    val reviewPagingItems = viewModel.reviewPagingData.collectAsLazyPagingItems()
    val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle(initialValue = null)
    val targetLanguage = currentReviewTranslationTargetLanguage()

    LaunchedEffect(refreshNonce) {
        if (refreshNonce == 0L) return@LaunchedEffect
        // 리뷰 작성 화면에서 돌아온 경우, 리스트/정보를 강제로 갱신한다.
        viewModel.getReview(menuType, id, menuName)
        reviewPagingItems.refresh()
    }

    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is UiEvent.ShowToast -> context.showToast(event)
            is ReviewListEvent.ReviewDeleted -> {
                context.showToast(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.toast_review_delete_success),
                        ToastType.SUCCESS
                    )
                )
                reviewPagingItems.refresh()
            }

            else -> {}
        }
    }

    ReviewListScreen(
        uiState = reviewListState,
        translationStates = translationStates,
        reviewPagingItems = reviewPagingItems,
        modifier = modifier,
        menuName = menuName,
        restaurant = restaurant,
        onBack = onBack,
        onReviewWriteButtonClick = onWriteButtonClick,
        onModifyClick = onModifyClick,
        onDeleteClick = { reviewId -> viewModel.deleteReview(reviewId) },
        onTranslationClick = { review ->
            targetLanguage?.let { viewModel.toggleReviewTranslation(review, it) }
        },
        targetLanguage = targetLanguage,
        isLoggedIn = viewModel.isLoggedIn,
    )
}

@Composable
internal fun ReviewListScreen(
    uiState: UiState<ReviewListState>,
    translationStates: Map<Long, ReviewTranslationUiState>,
    reviewPagingItems: LazyPagingItems<Review>,
    modifier: Modifier = Modifier,
    menuName: String,
    restaurant: Restaurant,
    onBack: () -> Unit = {},
    onReviewWriteButtonClick: () -> Unit,
    onModifyClick: (Review) -> Unit,
    onDeleteClick: (reviewId: Long) -> Unit,
    onTranslationClick: (Review) -> Unit,
    targetLanguage: String?,
    isLoggedIn: Boolean,
) {
    val context = LocalContext.current
    val analyticsTracker = LocalAnalyticsTracker.current

    var showMyBottomSheet by remember { mutableStateOf(false) }
    var showOthersBottomSheet by remember { mutableStateOf(false) }

    var selectedReview by remember { mutableStateOf<Review?>(null) }
    val reviewListScrollState = rememberLazyListState()

    if (showOthersBottomSheet && selectedReview != null) {
        OthersReviewBottomSheet(
            onDismiss = { showOthersBottomSheet = false; selectedReview = null },
            onReport = {
                val intent = Intent(context, ReportActivity::class.java)
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
                title = stringResource(R.string.review),
                onBack = onBack
            )
        },
        bottomBar = { // 하단에 버튼을 고정하기 위함
            EatSsuButton(
                text = stringResource(R.string.review_write),
                onClick = {
                    onReviewWriteButtonClick()
                    analyticsTracker.track(ReviewAnalyticsEvent.WriteClicked(restaurant))
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                when (uiState) {

                    is UiState.Init, UiState.Loading -> {
                        ReviewInfoContent(
                            menuName, ReviewInfo(
                                reviewCnt = 0,
                                fiveStarCount = 0,
                                fourStarCount = 0,
                                threeStarCount = 0,
                                twoStarCount = 0,
                                oneStarCount = 0,
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
                                    stringResource(R.string.review),
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
                        val info = uiState.data.reviewInfo
                        val displayMenuName = uiState.data.menuName ?: menuName

                        val loadState = reviewPagingItems.loadState
                        val isInitialLoading = loadState.refresh is LoadState.Loading
                        val isError = loadState.refresh is LoadState.Error

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = reviewListScrollState,
                        ) {
                            item {
                                ReviewInfoContent(displayMenuName, info)
                            }

                            item {
                                Spacer(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .fillMaxWidth()
                                        .height(16.dp)
                                        .background(Gray100)
                                )
                            }

                            item {
                                Row(Modifier.padding(horizontal = 24.dp)) {
                                    Text(
                                        stringResource(R.string.review),
                                        style = EatssuTheme.typography.h2,
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${info?.reviewCnt}",
                                        color = Primary,
                                        style = EatssuTheme.typography.h2,
                                    )
                                }
                            }

                            if (isInitialLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        DelayedLoadingIndicator(modifier = Modifier)
                                    }
                                }
                            } else if (isError) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                stringResource(R.string.toast_review_load_failed),
                                                style = EatssuTheme.typography.body1
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            EatSsuButton(
                                                text = stringResource(R.string.button_retry),
                                                onClick = { reviewPagingItems.retry() },
                                                modifier = Modifier.width(100.dp)
                                            )
                                        }
                                    }
                                }
                            } else if (reviewPagingItems.itemCount == 0) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        EmptyReviewContent(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                        )
                                    }
                                }
                            } else {
                                items(
                                    count = reviewPagingItems.itemCount,
                                    key = reviewPagingItems.itemKey { it.reviewId }
                                ) { index ->
                                    val item = reviewPagingItems.get(index)
                                    item?.let {
                                        val translationState = translationStates[it.reviewId]
                                        ReviewItem(
                                            modifier = Modifier.padding(
                                                horizontal = 24.dp,
                                            ),
                                            writeName = it.writerNickname,
                                            writeDate = it.writeDate,
                                            content = it.content,
                                            rating = it.rating,
                                            menuLikeInfoList = it.menuLikeInfoList,
                                            imgUrl = it.imgUrl,
                                            translatedContent = translationState?.translatedContent,
                                            isTranslationVisible = translationState?.isTranslated == true,
                                            isTranslationLoading = translationState?.isLoading == true,
                                            isTranslationUnavailable = translationState?.isUnavailable == true,
                                            isParentScrolling = reviewListScrollState.isScrollInProgress,
                                            showTranslationAction = shouldShowReviewTranslationAction(
                                                targetLanguage = targetLanguage,
                                                isLoggedIn = isLoggedIn,
                                                content = it.content,
                                            ),
                                            onTranslationClick = { onTranslationClick(it) },
                                            onMoreClick = {
                                                if (it.isWriter) {
                                                    showMyBottomSheet = true
                                                    selectedReview = it
                                                } else {
                                                    showOthersBottomSheet = true
                                                    selectedReview = it
                                                }
                                            }
                                        )
                                    }
                                }

                                // Append Loading / Error
                                when (val appendState = loadState.append) {
                                    is LoadState.Loading -> {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                DelayedLoadingIndicator(modifier = Modifier)
                                            }
                                        }
                                    }

                                    is LoadState.Error -> {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(stringResource(R.string.review_additional_data_load_failed))
                                                EatSsuButton(
                                                    text = stringResource(R.string.button_retry),
                                                    onClick = { reviewPagingItems.retry() },
                                                    modifier = Modifier.width(100.dp)
                                                )
                                            }
                                        }
                                    }

                                    else -> {}
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
                                fiveStarCount = 0,
                                fourStarCount = 0,
                                threeStarCount = 0,
                                twoStarCount = 0,
                                oneStarCount = 0,
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
                                    stringResource(R.string.review),
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
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.review_error_occurred),
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
            .fillMaxWidth()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cafeteria_menu_selected),
                        modifier = Modifier.size(18.dp),
                        tint = Primary,
                        contentDescription = "map restaurant icon"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.today_menu),
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
                fiveRatingCount = info?.fiveStarCount ?: 0,
                fourRatingCount = info?.fourStarCount ?: 0,
                threeRatingCount = info?.threeStarCount ?: 0,
                twoRatingCount = info?.twoStarCount ?: 0,
                oneRatingCount = info?.oneStarCount ?: 0,
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
            stringResource(R.string.none_review),
            style = EatssuTheme.typography.subtitle2,
            color = Gray600
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.none_review_list_detail),
            style = EatssuTheme.typography.caption2,
            color = Gray600
        )
    }
}

@Composable
fun <T : Any> rememberPreviewPagingItems(
    pagingData: PagingData<T>
): LazyPagingItems<T> {
    val flow = remember {
        flowOf(pagingData)
    }
    return flow.collectAsLazyPagingItems()
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    val reviewList = List(5) { id ->
        Review(
            reviewId = id.toLong(),
            isWriter = false,
            menuLikeInfoList = emptyList(),
            writerNickname = "작성자 $id",
            rating = 5,
            writeDate = "2024.10.10",
            content = "맛있어요 $id",
            imgUrl = null
        )
    }

    val pagingData = PagingData.from(
        reviewList,
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false)
        )
    )

    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기",
            restaurant = Restaurant.HAKSIK,
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            onTranslationClick = {},
            targetLanguage = "EN",
            isLoggedIn = true,
            translationStates = emptyMap(),
            uiState = UiState.Success(
                ReviewListState(
                    reviewInfo = ReviewInfo(
                        reviewCnt = 5,
                        fiveStarCount = 5,
                        fourStarCount = 0,
                        threeStarCount = 0,
                        twoStarCount = 0,
                        oneStarCount = 0,
                        rating = 5.0,
                    ),
                )
            ),
            reviewPagingItems = rememberPreviewPagingItems(pagingData),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListLoadingPreview() {
    val pagingData = PagingData.empty<Review>(
        sourceLoadStates = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false)
        )
    )

    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기",
            restaurant = Restaurant.HAKSIK,
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            onTranslationClick = {},
            targetLanguage = "EN",
            isLoggedIn = true,
            translationStates = emptyMap(),
            uiState = UiState.Success(
                ReviewListState(
                    reviewInfo = ReviewInfo(
                        reviewCnt = 0,
                        fiveStarCount = 0,
                        fourStarCount = 0,
                        threeStarCount = 0,
                        twoStarCount = 0,
                        oneStarCount = 0,
                        rating = 0.0,
                    ),
                )
            ),
            reviewPagingItems = rememberPreviewPagingItems(pagingData),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListEmptyPreview() {
    val pagingData = PagingData.empty<Review>(
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = true),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = true)
        )
    )

    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기+닭고기+돼지고기+양고기",
            restaurant = Restaurant.HAKSIK,
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            onTranslationClick = {},
            targetLanguage = "EN",
            isLoggedIn = true,
            translationStates = emptyMap(),
            uiState = UiState.Success(
                ReviewListState(
                    reviewInfo = ReviewInfo(
                        reviewCnt = 0,
                        fiveStarCount = 0,
                        fourStarCount = 0,
                        threeStarCount = 0,
                        twoStarCount = 0,
                        oneStarCount = 0,
                        rating = 0.0,
                    ),
                )
            ),
            reviewPagingItems = rememberPreviewPagingItems(pagingData),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListErrorPreview() {
    val pagingData = PagingData.empty<Review>(
        sourceLoadStates = LoadStates(
            refresh = LoadState.Error(Exception("Error")),
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false)
        )
    )

    EatssuTheme {
        ReviewListScreen(
            menuName = "소고기+닭고기+돼지고기+양고기+오리고기",
            restaurant = Restaurant.HAKSIK,
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            onTranslationClick = {},
            targetLanguage = "EN",
            isLoggedIn = true,
            translationStates = emptyMap(),
            uiState = UiState.Error,
            reviewPagingItems = rememberPreviewPagingItems(pagingData),
        )
    }
}
