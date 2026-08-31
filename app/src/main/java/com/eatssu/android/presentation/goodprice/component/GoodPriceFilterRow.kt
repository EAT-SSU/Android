package com.eatssu.android.presentation.goodprice.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eatssu.common.enums.GoodPriceCategory
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White

/**
 * 착한가격업소 카테고리 필터 칩 목록 (가로 스크롤 가능, 단일 선택)
 * 명세 순서: 전체 / 한식 / 일식 / 양식 / 중식 / 베이커리 / 기타 순
 * 글꼴: EatssuTypography Body3, 볼드 처리는 선택된 경우에만 적용
 */
@Composable
fun GoodPriceFilterRow(
    selectedCategory: GoodPriceCategory,
    onCategorySelected: (GoodPriceCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = GoodPriceCategory.entries

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories, key = { it.name }) { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x1A949494),
                        spotColor = Color(0x1A949494),
                    )
                    .clip(CircleShape)
                    .background(if (isSelected) Primary else White)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) Color.Transparent else Gray300,
                        shape = CircleShape,
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onCategorySelected(category) },
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(id = category.displayNameResId),
                    style = if (isSelected) {
                        EatssuTheme.typography.body2
                    } else {
                        EatssuTheme.typography.body3
                    },
                    color = if (isSelected) White else Gray400,
                )
            }
        }
    }
}
