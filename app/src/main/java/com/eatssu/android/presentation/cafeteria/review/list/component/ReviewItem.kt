package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.design_system.component.Chip
import com.eatssu.design_system.component.RatingBarSmall
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray400


@Composable
fun ReviewItem(
    writeName: String,
    writeDate: String,
    content: String,
    rating: Int,
    modifier: Modifier = Modifier,
    menuLikeInfoList: List<Review.MenuLikeInfo>? = null,
    imgUrl: String? = null,
    onMoreClick: () -> Unit = {}, // 바텀시트 열기 콜백
) {
    Column(modifier = modifier.padding(vertical = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = com.eatssu.design_system.R.drawable.ic_profile_24),
                contentDescription = "Profile Image",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    writeName,
                    style = EatssuTheme.typography.caption1
                )
                Spacer(modifier = Modifier.height(2.dp))
                RatingBarSmall(rating = rating)
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .padding(18.dp) // 터치 영역 확장 ( (48 - 12) / 2 )
                        .offset(x = 18.dp, y = 18.dp) // 시각 위치 되돌리기
                        .clickable(
                            onClick = onMoreClick,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu_12),
                        contentDescription = "etc",
                        modifier = Modifier.size(12.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = writeDate,
                    style = EatssuTheme.typography.caption3,
                    color = Gray400
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!menuLikeInfoList.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                modifier = Modifier.padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                menuLikeInfoList.forEach {
                    Chip(
                        menuName = it.name,
                        modifier = Modifier.padding(end = 4.dp, bottom = 2.dp),
                        isLike = it.isLike
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(content, style = EatssuTheme.typography.body3)

        // 이미지가 있는 경우에만 표시
        if (!imgUrl.isNullOrBlank() && imgUrl != "null") {
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = imgUrl,
                contentDescription = "Review image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@Preview
@Composable
fun ReviewItemPreview() {
    EatssuTheme {
        ReviewItem(
            modifier = Modifier,
            writeName = "숭실푸드파이터",
            writeDate = "2024-12-31",
            content = "맛있어요",
            rating = 4,
            menuLikeInfoList = listOf(
                Review.MenuLikeInfo(
                    menuId = 1L,
                    name = "소고기",
                    isLike = true
                ), Review.MenuLikeInfo(
                    menuId = 2L,
                    name = "닭고기",
                    isLike = false
                )
            ),
            imgUrl = "https://www.adobe.com/kr/creativecloud/photography/hub/features/media_19243bf806dc1c5a3532f3e32f4c14d44f81cae9f.jpeg?width=1200&format=pjpg&optimize=medium"
        )
    }
}

@Preview
@Composable
fun ReviewItemWithoutImagePreview() {
    EatssuTheme {
        ReviewItem(
            modifier = Modifier,
            writeName = "맛있는리뷰어",
            writeDate = "2024-12-30",
            content = "사진 없이 텍스트만 있는 리뷰입니다.",
            rating = 5,
            menuLikeInfoList = listOf(
                Review.MenuLikeInfo(
                    menuId = 1L,
                    name = "소고기",
                    isLike = true
                ), Review.MenuLikeInfo(
                    menuId = 2L,
                    name = "닭고기",
                    isLike = false
                )
            ),
            imgUrl = null
        )
    }
}