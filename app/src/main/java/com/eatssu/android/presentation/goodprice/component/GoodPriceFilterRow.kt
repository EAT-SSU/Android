package com.eatssu.android.presentation.goodprice.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eatssu.common.enums.GoodPriceCategory
import com.eatssu.design_system.theme.EatssuTheme
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
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories, key = { it.name }) { category ->
            val isSelected = category == selectedCategory

            // 필터 칩 아이템 (테두리 없는 깔끔한 캡슐형 칩)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) Primary else White)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 20.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = category.displayName,
                    // Body3 글꼴을 사용하며, 선택된 경우에만 Bold 처리
                    style = EatssuTheme.typography.body3.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    ),
                    color = if (isSelected) White else Color(0xFF8E8E93),
                )
            }
        }
    }
}
