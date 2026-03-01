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
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent

import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray600
import timber.log.Timber

@Composable
fun MyReviewListRoute(
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

    ObserveUiEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> context.showToast(event)
        }
    }

    MyReviewListScreen(
        uiState = reviewListState,
        userNickname = userNickname,
        modifier = modifier,
        onBack = onBack,
        onDeleteClick = { reviewId -> viewModel.deleteReview(reviewId) },
        onModifyClick = onModifyClick,
    )
}

@Composable
internal fun MyReviewListScreen(
    uiState: MyReviewUiState,
    userNickname: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onModifyClick: (Review) -> Unit,
    onDeleteClick: (reviewId: Long) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<Review?>(null) }

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
                    is MyReviewUiState.Success -> {
                        Timber.d("리뷰 존재")
                        val reviewList = uiState.reviews

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 24.dp),
                        ) {
                            items(reviewList) { item ->
                                ReviewItem(
                                    modifier = Modifier,
                                    writeName = userNickname,
                                    writeDate = item.writeDate,
                                    content = item.content,
                                    rating = item.rating,
                                    menuLikeInfoList = item.menuLikeInfoList,
                                    imgUrl = item.imgUrl,
                                    onMoreClick = {
                                        selectedReview = item
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }
                    }

                    is MyReviewUiState.Empty -> {
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

                    MyReviewUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    MyReviewUiState.Error -> {
                        // TODO: 에러 UI
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }


            }
        }
    }
}


@Preview
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        MyReviewListScreen(
            userNickname = "숭실푸드파이터",
            onDeleteClick = {},
            onModifyClick = {},
            uiState = MyReviewUiState.Success(
                reviews = emptyList(),
            ),
        )
    }
}

@Preview
@Composable
fun ReviewListEmptyPreview() {
    EatssuTheme {
        MyReviewListScreen(
            userNickname = "숭실푸드파이터",
            onDeleteClick = {},
            onModifyClick = {},
            uiState = MyReviewUiState.Empty
        )
    }
}
