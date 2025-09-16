package com.eatssu.design_system.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eatssu.design_system.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray400

@Composable
private fun SimpleFlowRow(
    horizontalSpacing: androidx.compose.ui.unit.Dp,
    verticalSpacing: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    Layout(content = content) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val maxWidth = constraints.maxWidth
        var currentRowWidth = 0
        var currentRowHeight = 0
        var totalHeight = 0
        val positions = mutableListOf<androidx.compose.ui.unit.IntOffset>()

        var x = 0
        var y = 0

        placeables.forEach { placeable ->
            val itemWidth = placeable.width
            val itemHeight = placeable.height

            if (x > 0 && x + itemWidth > maxWidth) {
                // wrap to next line
                y += currentRowHeight + verticalSpacing.roundToPx()
                x = 0
                currentRowHeight = 0
            }

            positions.add(androidx.compose.ui.unit.IntOffset(x, y))
            x += itemWidth + horizontalSpacing.roundToPx()
            currentRowHeight = maxOf(currentRowHeight, itemHeight)
            currentRowWidth = maxOf(currentRowWidth, x)
        }

        totalHeight = y + currentRowHeight

        layout(width = maxWidth, height = totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                val pos = positions[index]
                placeable.placeRelative(pos.x, pos.y)
            }
        }
    }
}

@Composable
fun ReviewItem(
    isWriter: Boolean,
    modifier: Modifier = Modifier,
    writeName: String,
    writeDate: String,
    content: String,
    rating: Int,
    menuList: List<String>? = null,
    likeMenuList: List<String>? = null,
    imgUrl: String? = null,
    onMoreClick: () -> Unit = {}, // 바텀시트 열기 콜백
) {
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_24),
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
                RatingBarSmall(rating = rating)
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = {
                        if (isWriter) onMoreClick()
                        else { //todo 신고하기
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_three_dot),
                        contentDescription = "etc",
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.Unspecified,
                    )
                }

                Text(
                    writeDate,
                    style = EatssuTheme.typography.caption3,
                    color = Gray400,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 태그 표시: likeMenuList는 좋아요 아이콘 표시, menuList 중 likeMenuList에 없는 항목은 아이콘 없이 표시
        val liked = likeMenuList.orEmpty()
        val allMenus = menuList.orEmpty()
        val others = allMenus.filter { it !in liked }

        if (liked.isNotEmpty() || others.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            SimpleFlowRow(horizontalSpacing = 4.dp, verticalSpacing = 2.dp) {
                liked.forEach { likedMenu ->
                    Chip(
                        menuName = likedMenu,
                        modifier = Modifier.padding(end = 4.dp, bottom = 2.dp),
                        isLike = true
                    )
                }

                others.forEach { menu ->
                    Chip(
                        menuName = menu,
                        modifier = Modifier.padding(end = 4.dp, bottom = 2.dp),
                        isLike = false
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


@Preview(showBackground = true)
@Composable
fun ReviewItemPreview() {
    EatssuTheme {
        ReviewItem(
            modifier = Modifier,
            isWriter = true,
            writeName = "숭실푸드파이터",
            writeDate = "2024-12-31",
            content = "맛있어요",
            rating = 4,
            menuList = listOf("소고기", "닭고기"),
            likeMenuList = listOf("소고기", "닭고기"),
            imgUrl = "https://www.adobe.com/kr/creativecloud/photography/hub/features/media_19243bf806dc1c5a3532f3e32f4c14d44f81cae9f.jpeg?width=1200&format=pjpg&optimize=medium"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewItemWithoutImagePreview() {
    EatssuTheme {
        ReviewItem(
            modifier = Modifier,
            isWriter = true,
            writeName = "맛있는리뷰어",
            writeDate = "2024-12-30",
            content = "사진 없이 텍스트만 있는 리뷰입니다.",
            rating = 5,
            menuList = listOf("소고기", "닭고기"),
            likeMenuList = null,
            imgUrl = null
        )
    }
}