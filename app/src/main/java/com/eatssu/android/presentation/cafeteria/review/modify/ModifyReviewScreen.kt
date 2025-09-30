package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.review.write.component.MenuLikeButtonItem
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.RatingBarMedium
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Primary
import timber.log.Timber

const val MAX_TEXT_COUNT = 300

@Composable
fun ModifyReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: ModifyViewModel = hiltViewModel(),
    reviewId: Long,
    initialRating: Int = 0,
    initialContent: String = "",
    menuList: List<Review.Menu> = emptyList(),
    onBack: () -> Unit = {},
    navController: NavController,
) {

    val reviewWriteState by viewModel.uiState.collectAsStateWithLifecycle()
    val menuLikeList by viewModel.menus.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.setInitialMenus(menuList)
    }

    val context = LocalContext.current

    // 리뷰 작성 성공 시 이전 화면으로 돌아가기
    LaunchedEffect(reviewWriteState) {
        when (reviewWriteState) {
            is UiState.Error -> {
                Timber.d("리뷰 작성 오류")
            }

            UiState.Init, UiState.Loading -> {
            }

            is UiState.Success -> {
                navController.popBackStack()

            }
        }
    }

    ModifyReviewScreen(
        menuList = menuLikeList,
        onBack = onBack,
        initialRating = initialRating,
        initialContent = initialContent,
        uiState = reviewWriteState,
        modifier = modifier,
        modifyDoneButtonClick = { rating, content, menuLikes ->
            viewModel.modifyMyReview(
                reviewId = reviewId,
                rating = rating,
                content = content,
                menuLikes = menuLikes,
            )
        },
        onChangeLike = { menuId -> viewModel.toggleLike(menuId) },
    )
}

@Composable
internal fun ModifyReviewScreen(
    menuList: List<Review.Menu>,
    onBack: () -> Unit,
    initialRating: Int = 0,
    initialContent: String = "",
    uiState: UiState<ModifyState>,
    modifier: Modifier = Modifier,
    modifyDoneButtonClick: (rating: Int, content: String, menuLikes: List<Review.Menu>) -> Unit,
    onChangeLike: (menuId: Long) -> Unit,
) {

    var rating by remember { mutableIntStateOf(initialRating) }
    var text by remember { mutableStateOf(initialContent) }

    Scaffold(
        topBar = {
            CloseTopBar("리뷰 수정하기", onClose = { onBack() })
        },
        bottomBar = {    // 하단에 버튼을 고정하기 위함
            EatSsuButton(
                text = "완료하기",
                enabled = rating != 0,
                onClick = {
                    modifyDoneButtonClick(
                        rating,
                        text,
                        menuList
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

                LazyColumn {
                    items(
                        items = menuList,
                        key = { menu -> menu.menuId } // 여기서 id 접근 가능해야 함
                    ) { menu ->
                        MenuLikeButtonItem(
                            modifier = Modifier.fillMaxWidth(),
                            mealName = menu.name,
                            isLiked = menu.isLike,
                            onLikeChanged = { onChangeLike(menu.menuId) }
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
                        text = "${text.length}/$MAX_TEXT_COUNT",
                        color = Gray400,
                        style = EatssuTheme.typography.caption3
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        ModifyReviewScreen(
            onBack = {},
            menuList = listOf(
                Review.Menu(1, "된장찌개", true),
                Review.Menu(2, "김치찌개", false),
                Review.Menu(3, "계란말이", true),
                Review.Menu(4, "돈까스", false),
                Review.Menu(5, "라면", false),
                Review.Menu(6, "피자", true),
                Review.Menu(7, "샐러드", false),
                Review.Menu(8, "과일", true),
            ),
            uiState = UiState.Success(ModifyState.ModifyDone),
            modifyDoneButtonClick = { _, _, _ -> },
            onChangeLike = {},
        )
    }
}