package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.eatssu.android.presentation.UiState
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.LikeButton
import com.eatssu.design_system.component.RatingBarMedium
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary
import timber.log.Timber

@Composable
fun ModifyReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: ModifyViewModel = hiltViewModel(),
    reviewId: Long,
    initialRating: Int = 0,
    initialContent: String = "",
    menuList: List<Pair<Long, String>> = emptyList(),
    likedNames: List<String> = emptyList(),
    onBack: () -> Unit = {},
    navController: NavController,
) {

    val reviewWriteState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val initialLikedSet = likedNames.toSet()

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
        menuList = menuList,
        onBack = onBack,
        initialRating = initialRating,
        initialContent = initialContent,
        initialLikedNames = likedNames,
        uiState = reviewWriteState,
        modifier = modifier,
        writeReviewButtonClick = { rating, content, menuLikes ->
            viewModel.modifyMyReview(
                reviewId = reviewId,
                rating = rating,
                content = content,
                menuLikes = menuLikes,
            )
        }
    )
}

@Composable
internal fun ModifyReviewScreen(
    menuList: List<Pair<Long, String>>,
    onBack: () -> Unit,
    initialRating: Int = 0,
    initialContent: String = "",
    initialLikedNames: List<String> = emptyList(),
    uiState: UiState<ModifyState>,
    modifier: Modifier = Modifier,
    writeReviewButtonClick: (rating: Int, content: String, menuLikes: List<Long>) -> Unit,
) {

    var rating by remember { mutableIntStateOf(initialRating) }
    var text by remember { mutableStateOf(initialContent) }
    var likedNameSet by remember { mutableStateOf(initialLikedNames.toSet()) }


    Scaffold(
        topBar = {
            CloseTopBar("리뷰 수정하기", onClose = { onBack() })
        },
        bottomBar = {    // 하단에 버튼을 고정하기 위함
            EatSsuButton(
                text = "완료하기",
                onClick = {
                    val menuLikesList = menuList
                        .filter { likedNameSet.contains(it.second) }
                        .map { it.first }
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


                SimpleFlowRow(horizontalSpacing = 4.dp, verticalSpacing = 8.dp) {
                    menuList.forEach { pair ->
                        val name = pair.second
                        val isLiked = likedNameSet.contains(name)
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            border = BorderStroke(0.5.dp, Primary),
                            color = Secondary,
                            contentColor = Primary,
                            modifier = Modifier
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isLiked) {
                                    Icon(
                                        painter = painterResource(id = com.eatssu.design_system.R.drawable.ic_thumb_up),
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    name,
                                    style = EatssuTheme.typography.caption3,
                                    color = Primary
                                )
                            }
                        }
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
                        text = "${text.length}/$maxChar",
                        color = Gray400,
                        style = EatssuTheme.typography.caption3
                    )
                }
            }

        }
    }
}

@Composable
private fun SimpleFlowRow(
    horizontalSpacing: androidx.compose.ui.unit.Dp,
    verticalSpacing: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    Layout(content = content) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val maxWidth = constraints.maxWidth
        var x = 0
        var y = 0
        var rowHeight = 0
        val positions = mutableListOf<androidx.compose.ui.unit.IntOffset>()

        placeables.forEach { p ->
            if (x > 0 && x + p.width > maxWidth) {
                x = 0
                y += rowHeight + verticalSpacing.roundToPx()
                rowHeight = 0
            }
            positions.add(androidx.compose.ui.unit.IntOffset(x, y))
            x += p.width + horizontalSpacing.roundToPx()
            rowHeight = maxOf(rowHeight, p.height)
        }

        val height = y + rowHeight
        layout(width = maxWidth, height = height) {
            placeables.forEachIndexed { index, placeable ->
                val pos = positions[index]
                placeable.placeRelative(pos.x, pos.y)
            }
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

    Row(modifier.padding(vertical = 6.dp)) {
        Text(
            mealName,
            style = EatssuTheme.typography.body3
        )
        Spacer(modifier = Modifier.weight(1f))
        LikeButton(
            isLiked = isLiked,
            onClick = {
                onLikeChanged(!isLiked)
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        ModifyReviewScreen(
            onBack = {},
            menuList = listOf(
                1L to "맑은 미역국",
                2L to "연탄불맛돈불고기",
                3L to "김말이",
            ),
            uiState = UiState.Success(ModifyState.ModifyDone),
            writeReviewButtonClick = { _, _, _ -> }
        )
    }
}