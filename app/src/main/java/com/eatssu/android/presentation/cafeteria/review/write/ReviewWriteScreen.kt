package com.eatssu.android.presentation.cafeteria.review.write

import EatSsuButton
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.list.component.RatingBar
import com.eatssu.android.presentation.cafeteria.review.write.component.LikeButton
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray100
import com.eatssu.android.presentation.compose.ui.theme.Gray200
import com.eatssu.android.presentation.compose.ui.theme.Gray300
import com.eatssu.android.presentation.compose.ui.theme.Gray400
import com.eatssu.android.presentation.compose.ui.theme.Gray500
import com.eatssu.android.presentation.compose.ui.theme.Primary
import timber.log.Timber

@Composable
fun ReviewWriteScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewWriteViewModel = hiltViewModel(),
    menuName: String,
    menuType: MenuType,
    id: Long,
) {
    Timber.d("넘어온 메뉴명: $menuName, 메뉴타입: $menuType, ID: $id")

    val reviewWriteState by viewModel.uiState.collectAsStateWithLifecycle()
    val viewModelMenuList by viewModel.menuList.collectAsStateWithLifecycle()

    // menuList를 Pair<Long, String> 리스트로 통일
    val menuList = remember(menuName, menuType, viewModelMenuList) {
        when (menuType) {
            MenuType.FIXED -> {
                // 고정 메뉴인 경우, Pair(id, menuName) 형태로 리스트를 만듭니다.
                listOf(Pair(id, menuName))
            }
            MenuType.VARIABLE -> {
                // 변동 메뉴는 이미 Pair 리스트이므로 그대로 사용합니다.
                viewModelMenuList
            }
        }
    }

    LaunchedEffect(menuType, id) {
        when (menuType) {
            MenuType.FIXED -> {
                Timber.d("고정 메뉴 - 원본 메뉴명: $menuName")
            }
            MenuType.VARIABLE -> {
                viewModel.findMenuItemByMealId(id)
            }
        }
    }

// menuList가 변경될 때마다 로그 출력
    LaunchedEffect(menuList) {
        Timber.d("최종 메뉴 목록: $menuList")
    }

    ReviewWriteScreen(
        menuList = menuList,
        uiState = reviewWriteState,
        modifier = modifier,
        addPhotoButtonClick = {

        },
        writeReviewButtonClick = { rating, content, menuLikes ->
            viewModel.postReview(
                menuType = menuType,
                itemId = id,
                rating = rating,
                content = content,
                menuLikes = menuLikes
            )
        }
    )

    val mealId by remember { mutableIntStateOf(13) }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewWriteScreen(
    menuList: List<Pair<Long, String>>,
    uiState: UiState<WriteReviewState>,
    modifier: Modifier = Modifier,
    addPhotoButtonClick: () -> Unit,
    writeReviewButtonClick: (rating: Int, content: String, menuLikes: List<Long>) -> Unit,
) {

    var rating by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }
    var likedMenus by remember { mutableStateOf(mutableListOf<Long>()) }
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text("리뷰 작성하기")

            Text(
                "오늘의 식사는 어뗘셨나요?",
                style = EatssuTheme.typography.subtitle1
            )

            RatingBar(
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                isBig = true,
                rating = rating, // 현재 상태 값 전달
                maxRating = 5,
                onRatingChanged = { newRating ->
                    rating = newRating // 클릭 시 상태 값 업데이트
                },
            )

            Text(
                "추천하고 싶은 메뉴가 있나요?",
                style = EatssuTheme.typography.subtitle1
            )

            Spacer(modifier = Modifier.height(16.dp))


                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // 이 부분이 중요합니다.
                        .fillMaxWidth()
                ) {
                items(menuList) { menuName ->
                    MenuItem(
                        mealName = menuName.second,
                        modifier = Modifier,
                        isLiked = likedMenus.contains(menuName.first),
                        onLikeChanged = { isLiked ->
                            if (isLiked) {
                                likedMenus.add(menuName.first)
                            } else {
                                likedMenus.remove(menuName.first)
                            }
                        }
                    )
                }
            }

            // 최대 글자 수
            val maxChar = 300


            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    value = text,
                    onValueChange = { newText ->
                        // 최대 글자 수를 초과하지 않도록 함
                        if (newText.length <= maxChar) {
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

                        // 테두리 색상 지정 🎨
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
                    text = "${text.length}/$maxChar",
                    color = Gray400,
                    style = EatssuTheme.typography.caption3
                )
            }

            //사진
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        addPhotoButtonClick()
                    },
                horizontalAlignment = Alignment.Start,
            ) {
                Column(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(5.dp)) // 먼저 클립하여 모양을 정의
                        .background(Gray100) // 연한 회색 배경 (예시)
                        // 테두리 추가 📏
                        .border(
                            width = 1.dp, // 테두리 두께
                            color = Gray200, // 테두리 색상
                            shape = RoundedCornerShape(5.dp) // 테두리도 같은 둥근 모양으로 적용
                        ),
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

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "사진 클릭 시, 삭제됩니다.",
                    color = Gray500,
                    style = EatssuTheme.typography.caption3
                )
            }
        }


        // 하단 고정 버튼
            EatSsuButton(
                text = "리뷰 작성하기",
                onClick = {
                    val menuLikesList = likedMenus.map { it }

                    writeReviewButtonClick(
                        rating,
                        text,
                        menuLikesList
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }
}


@Composable
fun MenuItem(
    modifier: Modifier,
    mealName: String,
    isLiked: Boolean,
    onLikeChanged: (Boolean) -> Unit,
) {

    Row(Modifier.padding(vertical = 6.dp)) {
        Text(
            mealName,
            style = EatssuTheme.typography.body3
        )
        Spacer(modifier = Modifier.weight(1f))
        LikeButton(
            isLiked = isLiked,
            onClick = {
                onLikeChanged(!isLiked) // 클릭 시 상태를 반전
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        ReviewWriteScreen(
            menuList = listOf(
                1L to "맑은 미역국",
                2L to "연탄불맛돈불고기",
                3L to "김말이",
            ),
            uiState = UiState.Success(WriteReviewState.Init),
            addPhotoButtonClick = {},
            writeReviewButtonClick = { _, _, _ -> }
        )
    }
}