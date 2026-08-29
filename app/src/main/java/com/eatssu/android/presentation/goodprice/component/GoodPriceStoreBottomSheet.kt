package com.eatssu.android.presentation.goodprice.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eatssu.android.R
import com.eatssu.android.domain.model.GoodPriceStoreDetail
import com.eatssu.android.presentation.map.component.MapLinkButtons
import com.eatssu.common.enums.GoodPriceCategory
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.White

/**
 * 착한가격업소 상세 정보 바텀시트
 * 글꼴 가이드:
 * - 식당명: H2
 * - 식당타입: Body2
 * - 주소: Body1
 * - 메뉴: Body3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodPriceStoreBottomSheet(
    storeDetail: GoodPriceStoreDetail,
    onKakaoMapClick: () -> Unit,
    onNaverMapClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 카테고리별 마커 아이콘 리소스 매핑
    val iconRes = when (storeDetail.category) {
        GoodPriceCategory.BAKERY -> R.drawable.ic_map_marker_bakery
        GoodPriceCategory.ETC -> R.drawable.ic_map_marker_cafe
        else -> R.drawable.ic_map_marker_restaurant
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            // 좌측 정보 영역 (업소명, 카테고리, 주소, 메뉴/가격)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (!storeDetail.imageUrl.isNullOrBlank()) 16.dp else 0.dp),
            ) {
                // 식당명: H2
                Text(
                    text = storeDetail.storeName,
                    style = EatssuTheme.typography.h2,
                    color = Black,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 식당타입: Body2
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = storeDetail.category.displayName,
                        style = EatssuTheme.typography.body2,
                        color = Gray600,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 주소: Body1
                if (!storeDetail.roadAddress.isNullOrBlank()) {
                    Text(
                        text = storeDetail.roadAddress,
                        style = EatssuTheme.typography.body1,
                        color = Black,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 메뉴: Body3
                if (!storeDetail.mainMenu.isNullOrBlank() || storeDetail.price != null) {
                    val priceText = storeDetail.price?.let { " %,d원".format(it) } ?: ""
                    Text(
                        text = "${storeDetail.mainMenu ?: ""}$priceText",
                        style = EatssuTheme.typography.body3,
                        color = Gray600,
                    )
                }
            }

            // 우측 업소 대표 이미지 (URL이 존재하는 경우 표시)
            if (!storeDetail.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = storeDetail.imageUrl,
                    contentDescription = storeDetail.storeName,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        MapLinkButtons(
            onKakaoMapClick = onKakaoMapClick,
            onNaverMapClick = onNaverMapClick,
        )
    }
}
