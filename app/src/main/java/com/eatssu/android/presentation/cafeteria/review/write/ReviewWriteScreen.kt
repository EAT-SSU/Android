package com.eatssu.android.presentation.cafeteria.review.write

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun ReviewWriteScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewWriteViewModel = hiltViewModel()
) {

    val reviewWriteState by viewModel.uiState.collectAsStateWithLifecycle()

    val mealId by remember { mutableIntStateOf(13) }

    ReviewWriteScreen(
        uiState = reviewWriteState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewWriteScreen(
    uiState: UiState<WriteReviewState>,
    modifier: Modifier = Modifier,
) {

    val mealList = listOf("맑은 미역국", "연탄불맛돈불고기", "김말이 데리강정")

    var rating by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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


            LazyColumn {
                items(mealList) { item ->
                    MenuItem(
                        mealName = item,
                        modifier = Modifier,
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
                modifier = Modifier.fillMaxWidth(),
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
//        EatssuButton(
//            modifier = Modifier,
//           title =  "리뷰 작성하기",
//           onClick =  {},
//        )
    }
}


@Composable
fun MenuItem(
    modifier: Modifier,
    mealName: String,
    ) {

    var isLiked by remember { mutableStateOf(false) }

    Row(Modifier.padding(vertical = 6.dp)) {
        Text(
            mealName,
            style = EatssuTheme.typography.body3
        )
        Spacer(modifier = Modifier.weight(1f))
        LikeButton(
            isLiked = isLiked,
            onClick = {
                isLiked = !isLiked // 클릭 시 상태를 반전
            }
        )
    }


}


@Preview(showBackground = true)
@Composable
fun ReviewListPreview() {
    EatssuTheme {
        ReviewWriteScreen(
            uiState = UiState.Success(),
        )
    }
}