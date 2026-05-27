package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.write.component.MenuLikeButtonItem
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuDialog
import com.eatssu.design_system.component.RatingBarMedium
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Primary

const val MAX_TEXT_COUNT = 300

@Composable
fun ModifyReviewScreen(
    reviewId: Long,
    initialRating: Int,
    modifier: Modifier = Modifier,
    viewModel: ModifyViewModel = hiltViewModel(),
    initialContent: String = "",
    menuLikeInfoList: List<Review.MenuLikeInfo> = emptyList(),
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    // Screen View 로깅
    TrackScreenViewEvent(ScreenId.REVIEW_V2_MODIFY)

    // 최초 1회 초기화
    LaunchedEffect(Unit) {
        viewModel.init(initialRating, initialContent, menuLikeInfoList)
    }

    // 완료 이펙트 처리
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> onBack()
                is UiEvent.ShowToast -> {
                    context.showToast(event)
                }
            }
        }
    }

    var showExitDialog by remember { mutableStateOf(false) }

    val hasUnsavedChanges = (ui as? UiState.Success)?.data?.let {
        (it as? ModifyState.Editing)?.hasChanges ?: false
    } ?: false

    val handleBack = {
        if (hasUnsavedChanges) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    BackHandler(enabled = true) {
        handleBack()
    }

    if (showExitDialog) {
        EatSsuDialog(
            title = stringResource(R.string.review_exit_dialog_title),
            description = stringResource(R.string.review_exit_dialog_description),
            confirmText = stringResource(R.string.review_exit_dialog_confirm),
            dismissText = stringResource(R.string.review_exit_dialog_dismiss),
            onConfirmClick = { showExitDialog = false },
            onDismissButtonClick = {
                showExitDialog = false
                onBack()
            },
            onDismissRequest = { showExitDialog = false },
            visible = showExitDialog
        )
    }

    when (val data = (ui as? UiState.Success)?.data) {
        is ModifyState.Editing -> {
            ModifyReviewScreen(
                modifier = modifier,
                title = stringResource(R.string.title_review_modify),
                rating = data.rating,
                content = data.content,
                menuLikeInfos = data.menuLikeInfos,
                isSubmitting = false,
                canSubmit = data.canSubmit,
                onBack = handleBack,
                onRatingChanged = viewModel::onRatingChanged,
                onContentChanged = { new ->
                    if (new.length <= MAX_TEXT_COUNT) viewModel.onContentChanged(new)
                },
                onToggleLike = viewModel::toggleLike,
                onSubmit = { viewModel.submit(reviewId) }
            )
        }

        is ModifyState.Modifying -> {
            // 통신 중에도 폼은 유지, 버튼/입력 제한만
            ModifyReviewScreen(
                modifier = modifier,
                title = stringResource(R.string.title_review_modify),
                rating = data.rating,
                content = data.content,
                menuLikeInfos = data.menuLikeInfos,
                isSubmitting = true,
                canSubmit = false,
                onBack = handleBack,
                onRatingChanged = {},          // 수정 불가
                onContentChanged = {},         // 수정 불가
                onToggleLike = {},             // 수정 불가
                onSubmit = {}                  // 중복 제출 방지
            )
        }

        else -> {
            // 에러나 초기 로딩 등: 최소 로딩 UI
            Surface(modifier = modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.review_preparing), style = EatssuTheme.typography.body2)
                }
            }
        }
    }
}

@Composable
internal fun ModifyReviewScreen(
    modifier: Modifier = Modifier,
    title: String,
    rating: Int,
    content: String,
    menuLikeInfos: List<Review.MenuLikeInfo>,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    onBack: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onContentChanged: (String) -> Unit,
    onToggleLike: (Long) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(
        topBar = { CloseTopBar(title, onClose = onBack) },
        bottomBar = {
            EatSsuButton(
                text = if (isSubmitting) stringResource(R.string.review_modifying) else stringResource(R.string.button_complete),
                enabled = canSubmit && rating > 0 && !isSubmitting,
                onClick = onSubmit,
                modifier = Modifier.padding(24.dp)
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.review_how_was_meal), style = EatssuTheme.typography.subtitle1)

                RatingBarMedium(
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                    rating = rating,
                    onRatingChanged = { if (!isSubmitting) onRatingChanged(it) }
                )

                Text(stringResource(R.string.review_recommend_menu), style = EatssuTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // 본문 스크롤, 버튼 고정
                ) {
                    items(items = menuLikeInfos, key = { it.menuId }) { menu ->
                        MenuLikeButtonItem(
                            modifier = Modifier.fillMaxWidth(),
                            mealName = menu.name,
                            isLiked = menu.isLike,
                            onLikeChanged = {
                                if (!isSubmitting) onToggleLike(menu.menuId)
                            }
                        )
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        Column {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                value = content,
                                onValueChange = { new ->
                                    if (!isSubmitting && new.length <= MAX_TEXT_COUNT) {
                                        onContentChanged(new)
                                    }
                                },
                                placeholder = {
                                    Text(
                                        stringResource(R.string.review_placeholder),
                                        style = EatssuTheme.typography.body2,
                                        color = Gray400
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Gray100,
                                    unfocusedContainerColor = Gray100,
                                    unfocusedBorderColor = Gray200,
                                    focusedBorderColor = Gray200,
                                    unfocusedLabelColor = Gray400,
                                    focusedLabelColor = Gray400,
                                    cursorColor = Primary
                                )
                            )
                            Text(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 8.dp),
                                text = "${content.length}/$MAX_TEXT_COUNT",
                                color = Gray400,
                                style = EatssuTheme.typography.caption3
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ModifyReviewPreview() {
    EatssuTheme {
        ModifyReviewScreen(
            title = "리뷰 수정하기",
            rating = 3,
            content = "국밥 맛있음!",
            menuLikeInfos = listOf(
                Review.MenuLikeInfo(1, "된장찌개", true),
                Review.MenuLikeInfo(2, "김치찌개", false),
                Review.MenuLikeInfo(3, "계란말이", true),
                Review.MenuLikeInfo(4, "돈까스", false),
            ),
            isSubmitting = false,
            canSubmit = false,
            onBack = {},
            onRatingChanged = {},
            onContentChanged = {},
            onToggleLike = {},
            onSubmit = {}
        )
    }
}
