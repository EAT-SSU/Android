package com.eatssu.android.presentation.cafeteria.review.write

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.presentation.cafeteria.review.write.component.MenuLikeButtonItem
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuDialog
import com.eatssu.design_system.component.RatingBarMedium
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
import coil.compose.AsyncImage

const val MAX_TEXT_COUNT = 300

@Composable
fun WriteReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: WriteReviewViewModel = hiltViewModel(),
    menuName: String,
    menuType: MenuType,
    id: Long,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    // 갤러리 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.setSelectedImage(uri) }

    // Screen View 로깅
    TrackScreenViewEvent(ScreenId.REVIEW_V2_WRITE)

    // 처음 진입 시, 메뉴 불러오기: 기본찬(김치, 단무지, 밥) 등을 거르기 위함
    LaunchedEffect(menuType, id, menuName) {
        viewModel.loadMenuList(menuType, id, menuName)
    }

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

    val hasUnsavedChanges = when (val data = (ui as? UiState.Success)?.data) {
        is WriteReviewState.Editing -> data.rating > 0 || data.content.isNotEmpty() || data.selectedImageUri != null
        else -> false
    }

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
        is WriteReviewState.Editing -> {
            WriteReviewScreen(
                modifier = modifier,
                title = stringResource(R.string.title_review_write),
                menuList = data.menuList,
                rating = data.rating,
                content = data.content,
                likedMenuIds = data.likedMenuIds,
                selectedImageUri = data.selectedImageUri,
                isPosting = false,
                onBack = handleBack,
                onRatingChanged = viewModel::onRatingChanged,
                onContentChanged = { new ->
                    if (new.length <= MAX_TEXT_COUNT) viewModel.onContentChanged(new)
                },
                onToggleLike = viewModel::toggleLike,
                onImageSelect = { galleryLauncher.launch("image/*") },
                onImageDelete = { viewModel.setSelectedImage(null) },
                onSubmit = { viewModel.postReview(menuType, id, context) }
            )
        }

        is WriteReviewState.Posting -> {
            WriteReviewScreen(
                modifier = modifier,
                title = stringResource(R.string.title_review_write),
                menuList = data.menuList,
                rating = data.rating,
                content = data.content,
                likedMenuIds = data.likedMenuIds,
                selectedImageUri = data.selectedImageUri,
                isPosting = true,
                onBack = handleBack,
                onRatingChanged = {}, // 비활성
                onContentChanged = {}, // 비활성
                onToggleLike = {}, // 비활성
                onImageSelect = {}, // 비활성
                onImageDelete = {}, // 비활성
                onSubmit = {} // 중복 제출 방지
            )
        }

        else -> {
            Surface(modifier = modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))
                    Text(stringResource(R.string.review_preparing), style = EatssuTheme.typography.body2)
                }
            }
        }
    }
}

@Composable
internal fun WriteReviewScreen(
    modifier: Modifier = Modifier,
    title: String,
    menuList: List<MenuMini>,
    rating: Int,
    content: String,
    likedMenuIds: Set<Long>,
    selectedImageUri: Uri?,
    isPosting: Boolean,
    onBack: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onContentChanged: (String) -> Unit,
    onToggleLike: (Long) -> Unit,
    onImageSelect: () -> Unit,
    onImageDelete: () -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(
        topBar = { CloseTopBar(title, onClose = onBack) },
        bottomBar = {
            EatSsuButton(
                text = if (isPosting) stringResource(R.string.review_posting) else stringResource(R.string.button_complete),
                enabled = rating > 0 && !isPosting,
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
                    onRatingChanged = { if (!isPosting) onRatingChanged(it) }
                )

                Text(stringResource(R.string.review_recommend_menu), style = EatssuTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // 본문 스크롤, 버튼 고정
                ) {
                    items(menuList, key = { it.id }) { (id, name) ->
                        MenuLikeButtonItem(
                            modifier = Modifier.fillMaxWidth(),
                            mealName = name,
                            isLiked = id in likedMenuIds,
                            onLikeChanged = {
                                if (!isPosting) onToggleLike(id)
                            }
                        )
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        // 텍스트 입력
                        Column {
                            androidx.compose.material3.OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                value = content,
                                onValueChange = { new ->
                                    if (!isPosting && new.length <= MAX_TEXT_COUNT) {
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
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Gray100, //fill
                                    unfocusedContainerColor = Gray100, //fill
                                    unfocusedBorderColor = Gray200, //stroke
                                    focusedBorderColor = Gray200, //storke
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
                        Spacer(Modifier.height(16.dp))

                        // 사진
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (selectedImageUri != null) {
                                Column(
                                    modifier = Modifier
                                        .size(150.dp)
//                                        .clickable(enabled = !isPosting) { onImageDelete() }
                                ) {
                                    Box(
                                        modifier = Modifier.size(92.dp)
                                    ) {
                                        AsyncImage(
                                            model = selectedImageUri,
                                            contentDescription = "Selected image",
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                        )

                                        IconButton(
                                            onClick = { onImageDelete() },
                                            modifier = Modifier
                                                .size(24.dp) // 터치 영역
                                                .align(Alignment.TopEnd)
                                                .offset(x = 5.dp, y = (-5).dp) // 이미지 위에 살짝 겹치게
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_minus),
                                                contentDescription = "remove photo",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(20.dp) // 실제 아이콘
                                            )
                                        }
                                    }
                                }
                            } else {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(5.dp))
//                                        .background(White)
                                        .border(1.dp, Gray200, RoundedCornerShape(12.dp))
                                        .clickable(enabled = !isPosting) { onImageSelect() },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_camera_24),
                                        contentDescription = "add photo",
                                        tint = Gray700
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        stringResource(R.string.review_photo_count, 0, 1),
                                        color = Gray700,
                                        style = EatssuTheme.typography.body1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewWritePreview() {
    EatssuTheme {
        WriteReviewScreen(
            title = "리뷰 작성하기",
            menuList = listOf(
                MenuMini(1, "김치"), MenuMini(2, "계란말이"), MenuMini(3, "닭볶음탕")
            ),
            rating = 3,
            content = "맛있었습니다!",
            likedMenuIds = setOf(1L),
            selectedImageUri = null,
            isPosting = false,
            onBack = {},
            onRatingChanged = {},
            onContentChanged = {},
            onToggleLike = {},
            onImageSelect = {},
            onImageDelete = {},
            onSubmit = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun ReviewWritePreviewPhoto() {
    EatssuTheme {
        WriteReviewScreen(
            title = "리뷰 작성하기",
            menuList = listOf(
                MenuMini(1, "김치"), MenuMini(2, "계란말이"), MenuMini(3, "닭볶음탕")
            ),
            rating = 3,
            content = "맛있었습니다!",
            likedMenuIds = setOf(1L),
            selectedImageUri = "https://static.wtable.co.kr/image-resize/production/service/recipe/2167/4x3/c9d9173f-d3e1-43cd-871d-339614b0dbac.jpg".toUri(),
            isPosting = false,
            onBack = {},
            onRatingChanged = {},
            onContentChanged = {},
            onToggleLike = {},
            onImageSelect = {},
            onImageDelete = {},
            onSubmit = {}
        )
    }
}
