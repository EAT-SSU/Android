package com.eatssu.android.presentation.cafeteria.review.write

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.eatssu.android.R
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.presentation.cafeteria.review.write.component.MenuLikeButtonItem
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.MenuType
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.RatingBarMedium
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Primary

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

    // 처음 진입 시, 메뉴 불러오기: 기본찬(김치, 단무지, 밥) 등을 거르기 위함
    LaunchedEffect(menuType, id, menuName) {
        viewModel.loadMenus(menuType, id, menuName)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> onBack()
                is UiEvent.ShowToast -> {
                    context.showToast(event.message)
                }
            }
        }
    }

    when (val data = (ui as? UiState.Success)?.data) {
        is WriteReviewState.Editing -> {
            WriteReviewScreen(
                modifier = modifier,
                title = "리뷰 작성하기",
                menuList = data.menuList,
                rating = data.rating,
                content = data.content,
                likedMenuIds = data.likedMenuIds,
                selectedImageUri = data.selectedImageUri,
                isPosting = false,
                onBack = onBack,
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
                title = "리뷰 작성하기",
                menuList = data.menuList,
                rating = data.rating,
                content = data.content,
                likedMenuIds = data.likedMenuIds,
                selectedImageUri = data.selectedImageUri,
                isPosting = true,
                onBack = onBack,
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
                    Text("화면을 준비하는 중입니다.", style = EatssuTheme.typography.body2)
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
                text = if (isPosting) "작성 중..." else "완료하기",
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
                Text("오늘의 식사는 어땠나요?", style = EatssuTheme.typography.subtitle1)

                RatingBarMedium(
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                    rating = rating,
                    onRatingChanged = { if (!isPosting) onRatingChanged(it) }
                )

                Text("추천하고 싶은 메뉴가 있나요?", style = EatssuTheme.typography.subtitle1)
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
                                        "메뉴에 대한 상세한 리뷰를 작성해주세요",
                                        style = EatssuTheme.typography.body2,
                                        color = Gray400
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
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
                        Spacer(Modifier.height(16.dp))

                        // 사진
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (selectedImageUri != null) {
                                Column(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(enabled = !isPosting) { onImageDelete() }
                                ) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Selected image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = "사진 클릭 시, 삭제됩니다.",
                                    color = Gray500,
                                    style = EatssuTheme.typography.caption3
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Gray100)
                                        .border(1.dp, Gray200, RoundedCornerShape(5.dp))
                                        .clickable(enabled = !isPosting) { onImageSelect() },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_camera_light),
                                        contentDescription = "add photo",
                                        tint = Gray300
                                    )
                                    Text(
                                        "사진 0/1",
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
