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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.write.component.MenuLikeButtonItem
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
import timber.log.Timber

const val MAX_TEXT_COUNT = 300

@Composable
fun ReviewWriteScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewWriteViewModel = hiltViewModel(),
    menuName: String,
    menuType: MenuType,
    id: Long,
    onBack: () -> Unit,
) {
    Timber.d("넘어온 메뉴명: $menuName, 메뉴타입: $menuType, ID: $id")

    val reviewWriteState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()

    // 갤러리 선택을 위한 launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setSelectedImage(uri)
    }

    val context = LocalContext.current

    // menuList를 Pair<Long, String> 리스트로 통일
    LaunchedEffect(menuType, id, menuName) {
        when (menuType) {
            MenuType.FIXED -> {
                // 고정 메뉴인 경우, Pair(id, menuName) 형태로 리스트를 만듭니다.
                viewModel.returnMenuItem(id, menuName)
            }

            MenuType.VARIABLE -> {
                // 변동 메뉴는 이미 Pair 리스트이므로 그대로 사용합니다.
                viewModel.findMenuItemByMealId(id)
            }
        }
    }

    // 리뷰 작성 성공 시 이전 화면으로 돌아가기
    LaunchedEffect(reviewWriteState) {
        if (reviewWriteState is UiState.Success && (reviewWriteState as UiState.Success<WriteReviewState>).data == WriteReviewState.WriteDone) {
            onBack()
        }
    }

    ReviewWriteScreen(
        uiState = reviewWriteState,
        selectedImageUri = selectedImageUri,
        modifier = modifier,
        onImageSelect = {
            galleryLauncher.launch("image/*")
        },
        onImageDelete = {
            viewModel.setSelectedImage(null)
        },
        writeReviewButtonClick = { rating, content, menuLikes ->
            viewModel.postReview(
                menuType = menuType,
                itemId = id,
                rating = rating,
                content = content,
                menuLikes = menuLikes,
                context = context,
            )
        },
        onBack = onBack
    )
}

@Composable
internal fun ReviewWriteScreen(
    uiState: UiState<WriteReviewState>,
    selectedImageUri: Uri?,
    modifier: Modifier = Modifier,
    onImageSelect: () -> Unit,
    onImageDelete: () -> Unit,
    writeReviewButtonClick: (rating: Int, content: String, menuLikes: List<Long>) -> Unit,
    onBack: () -> Unit,
) {

    var rating by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }
    var likedMenus by remember { mutableStateOf(mutableListOf<Long>()) }

    // 갤러리 선택을 위한 launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // 콜백을 통해 메인 함수에서 처리
    }

    Scaffold(
        topBar = {
            CloseTopBar("리뷰 작성하기", onClose = { onBack() })
        },
        bottomBar = {    // 하단에 버튼을 고정하기 위함
            EatSsuButton(
                text = "완료하기",
                enabled = rating != 0,
                onClick = {
                    val menuLikesList = likedMenus.map { it }
                    writeReviewButtonClick(
                        rating,
                        text,
                        menuLikesList
                    )
                },
                modifier = Modifier
                    .padding(24.dp)
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "오늘의 식사는 어뗘셨나요?",
                    style = EatssuTheme.typography.subtitle1
                )

                RatingBarMedium(
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                    rating = rating, // 현재 상태 값 전달
                    onRatingChanged = { newRating ->
                        rating = newRating // 클릭 시 상태 값 업데이트
                    },
                )

                Text(
                    "추천하고 싶은 메뉴가 있나요?",
                    style = EatssuTheme.typography.subtitle1
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState) {

                    is UiState.Init, UiState.Loading -> {
                        // 리뷰 작성 메뉴 선별 api 응답 기다리는 중
                        // 초기 상태, 아무 것도 하지 않음
                    }

                    is UiState.Success -> {
                        when (uiState.data) {
                            is WriteReviewState.ValidMenuListForReview -> {
                                LazyColumn {
                                    items(uiState.data.menuList) { menu -> // 매개변수 이름을 menuPair로 변경하여 혼동 방지
                                        MenuLikeButtonItem(
                                            mealName = menu.second,
                                            modifier = Modifier,
                                            isLiked = likedMenus.contains(menu.first),
                                            onLikeChanged = { isLiked ->
                                                // Set을 사용하여 중복 제거 및 상태 변경
                                                val newSet = likedMenus.toSet()
                                                val updatedList = if (isLiked) {
                                                    (newSet + menu.first).toList()
                                                } else {
                                                    (newSet - menu.first).toList()
                                                }
                                                likedMenus =
                                                    updatedList.toMutableList() // mutableStateOf를 위해 MutableList로 다시 변환
                                            }
                                        )
                                    }
                                }

                                Column {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp),
                                        value = text,
                                        onValueChange = { newText ->
                                            // 최대 글자 수를 초과하지 않도록 함
                                            if (newText.length <= MAX_TEXT_COUNT) {
                                                text = newText
                                            }
                                        },
                                        label = {
                                            Text(
                                                "메뉴에 대한 상세한 리뷰를 작성해주세요",
                                                style = EatssuTheme.typography.body2
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            // 배경색
                                            focusedContainerColor = Gray100,
                                            unfocusedContainerColor = Gray100,

                                            // 테두리 색상
                                            unfocusedBorderColor = Gray200,
                                            focusedBorderColor = Gray200,

                                            // 힌트 문구 색상
                                            unfocusedLabelColor = Gray400,
                                            focusedLabelColor = Gray400,
                                            cursorColor = Primary
                                        )
                                    )

                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .padding(top = 8.dp),
                                        text = "${text.length}/$MAX_TEXT_COUNT",
                                        color = Gray400,
                                        style = EatssuTheme.typography.caption3
                                    )
                                }

                                //사진
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start,
                                ) {
                                    if (selectedImageUri != null) {
                                        // 선택된 이미지가 있는 경우
                                        Column(
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    onImageDelete()
                                                }
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
                                        // 이미지가 선택되지 않은 경우
                                        Column(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Gray100)
                                                .border(
                                                    width = 1.dp,
                                                    color = Gray200,
                                                    shape = RoundedCornerShape(5.dp)
                                                )
                                                .clickable {
                                                    onImageSelect()
                                                },
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_camera_light),
                                                "add photo",
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

                            is WriteReviewState.WriteDone -> {
//                                onBack()
                            }

                            null -> TODO()
                        }
                    }


                    is UiState.Error -> {
                        Timber.d("리뷰 작성 에러")
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
        ReviewWriteScreen(
            uiState = UiState.Success(WriteReviewState.WriteDone),
            selectedImageUri = null,
            onImageSelect = {},
            onImageDelete = {},
            writeReviewButtonClick = { _, _, _ -> },
            onBack = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreviewMenuList() {
    EatssuTheme {
        ReviewWriteScreen(
            uiState = UiState.Success(
                WriteReviewState.ValidMenuListForReview(
                    listOf(
                        1L to "맑은 미역국",
                        2L to "연탄불맛돈불고기",
                        3L to "김말이",
                    )
                )
            ),
            selectedImageUri = null,
            onImageSelect = {},
            onImageDelete = {},
            writeReviewButtonClick = { _, _, _ -> },
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListPreviewWrittenReview() {
    EatssuTheme {
        ReviewWriteScreen(
            uiState = UiState.Success(
                WriteReviewState.ValidMenuListForReview(
                    listOf(
                        1L to "맑은 미역국",
                        2L to "연탄불맛돈불고기",
                        3L to "김말이",
                    )
                )
            ),
            selectedImageUri = null,
            onImageSelect = {},
            onImageDelete = {},
            writeReviewButtonClick = { _, _, _ -> },
            onBack = {}
        )
    }
}