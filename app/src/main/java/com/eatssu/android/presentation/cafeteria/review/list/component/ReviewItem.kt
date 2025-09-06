package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray400

@Composable
fun ReviewItem(
    isWriter: Boolean,
    modifier: Modifier,
    writeName: String,
    writeDate: String,
    content: String,
    rating: Int,
    likeMenuList: List<String>?,
    imgUrl: String?,
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
                RatingBar(isBig = false, rating = rating, onRatingChanged = {}).also {
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_three_dot),
                    contentDescription = "etc",
                    modifier = Modifier
                        .size(24.dp)
                        .then(
                            if (isWriter) Modifier
                                .clickable { onMoreClick() }
                            else Modifier
                        ),
                    tint = Color.Unspecified,
                )
                Text(
                    writeDate,
                    style = EatssuTheme.typography.caption3,
                    color = Gray400,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 좋아하는 메뉴 태그들 (있는 경우에만 표시)
        if (!likeMenuList.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                likeMenuList.forEach { likedMenu ->
                    Tag(menuName = likedMenu, modifier = Modifier)
                    Spacer(modifier = Modifier.width(4.dp))
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
    ReviewItem(
        modifier = Modifier,
        isWriter = true,
        writeName = "숭실푸드파이터",
        writeDate = "2024-12-31",
        content = "맛있어요",
        rating = 4,
        likeMenuList = listOf("소고기", "닭고기"),
        imgUrl = "https://picsum.photos/400/300"
    )
}

@Preview(showBackground = true)
@Composable
fun ReviewItemWithoutImagePreview() {
    ReviewItem(
        modifier = Modifier,
        isWriter = true,
        writeName = "맛있는리뷰어",
        writeDate = "2024-12-30",
        content = "사진 없이 텍스트만 있는 리뷰입니다.",
        rating = 5,
        likeMenuList = null,
        imgUrl = null
    )
}