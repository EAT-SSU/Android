package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.map.iconRes
import com.eatssu.android.presentation.map.model.RestaurantInfo
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.StoreType
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapRestaurantBottomSheet(
    storeName: String,
    storeType: StoreType,
    mapRestaurantList: List<RestaurantInfo>,
    naverMapUrl: String? = null,
    kakaoMapUrl: String? = null,
    onNaverMapClick: () -> Unit = {},
    onKakaoMapClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hasNaverMapUrl = !naverMapUrl.isNullOrBlank()
    val hasKakaoMapUrl = !kakaoMapUrl.isNullOrBlank()

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    TrackScreenViewEvent(ScreenId.MAP_DETAIL)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(bottom = 40.dp)
        ) {
            // 상단 회색 바
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(30.dp)
                    .height(2.dp)
                    .background(
                        color = Gray400,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 상단 타이틀 라인 (store name + 하트)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = storeName,
                        style = EatssuTheme.typography.subtitle2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = storeType.iconRes),
                                contentDescription = storeName,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = storeType.value,
                            style = EatssuTheme.typography.caption3,
                            color = Gray400,
                        )
                    }
                }

                // 찜 기능
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.padding(bottom = 24.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_like),
//                        contentDescription = "좋아요",
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "0",
//                        style = EatssuTheme.typography.caption2,
//                        color = Gray600
//                    )
//                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 제휴 리스트
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
            ) {
                itemsIndexed(mapRestaurantList) { index, item ->
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = buildAnnotatedString {
                                when {
                                    item.collegeName != null && item.departmentName == null -> {
                                        append(item.collegeName)
                                    }
                                    item.collegeName == null && item.departmentName != null -> {
                                        append(item.departmentName)
                                    }
                                    item.collegeName != null && item.departmentName != null -> {
                                        append("${item.collegeName}${item.departmentName}")
                                    }
                                    else -> {
                                        append(stringResource(R.string.map_unknown_college_department))
                                    }
                                }

                                append("   ")
                                withStyle(style = SpanStyle(color = Gray500, fontSize = EatssuTheme.typography.caption3.fontSize)) {
                                    append(item.period)
                                }
                            },
                            style = EatssuTheme.typography.body2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.benefit,
                            style = EatssuTheme.typography.caption2,
                            color = Gray600
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (index < mapRestaurantList.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Gray200)
                                    .padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            if (hasKakaoMapUrl || hasNaverMapUrl) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gray200),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (hasKakaoMapUrl) {
                        MapLinkButton(
                            iconRes = R.drawable.ic_kakao_map,
                            labelRes = R.string.map_open_kakao,
                            onClick = onKakaoMapClick,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (hasKakaoMapUrl && hasNaverMapUrl) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(Gray400),
                        )
                    }

                    if (hasNaverMapUrl) {
                        MapLinkButton(
                            iconRes = R.drawable.ic_naver_map,
                            labelRes = R.string.map_open_naver,
                            onClick = onNaverMapClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapLinkButton(
    iconRes: Int,
    labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(labelRes),
            style = EatssuTheme.typography.body2,
            color = Gray600,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MapRestaurantBottomSheetPreview() {
    val dummyList = listOf(
        RestaurantInfo("경영대", null, "09.03~12.18","학생증 인증하면 음료수 1개 증정"),
        RestaurantInfo("IT대", null,"09.01~12.31", "학생증 인증하고 카카오페이 결제 시 10% 할인, 긴내용긴내용긴내용긴내용"),
        RestaurantInfo(null, "글로벌미디어학부", "09.03~12.18", "학생증 인증하면 음료수 1개 증정"),
        RestaurantInfo("공과대", null,"09.01~12.31", "학생증 인증하고 카카오페이 결제 시 10% 할인, 긴내용긴내용")
    )

    EatssuTheme {
        Surface {
            MapRestaurantBottomSheet(
                storeName = "현선이네",
                mapRestaurantList = dummyList,
                storeType = StoreType.RESTAURANT,
                naverMapUrl = "https://naver.me/test",
                kakaoMapUrl = "https://place.map.kakao.com/test",
            )
        }
    }
}
