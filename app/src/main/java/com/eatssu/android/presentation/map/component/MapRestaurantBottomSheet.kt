package com.eatssu.android.presentation.map.component

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
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
import com.eatssu.design_system.component.EatSsuSnackbar
import com.eatssu.design_system.component.EatSsuSnackbarType
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapRestaurantBottomSheet(
    storeName: String,
    storeType: StoreType,
    isLike: Boolean,
    mapRestaurantList: List<RestaurantInfo>,
    onNaverMapClick: () -> Unit = {},
    onKakaoMapClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var snackbarState by remember { mutableStateOf<Pair<String, String?>?>(null) }
    var snackbarJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    TrackScreenViewEvent(ScreenId.MAP_DETAIL)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        sheetState = sheetState,
        dragHandle = null,
        scrimColor = scrimColor,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = if (isLike) R.drawable.ic_like_filled else R.drawable.ic_like_line),
                        contentDescription = "좋아요",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val wasLiked = isLike
                                onLikeClick()
                                snackbarJob?.cancel()
                                snackbarJob = scope.launch {
                                    snackbarState = if (!wasLiked) {
                                        context.getString(R.string.favorite_added_snackbar) to null
                                    } else {
                                        context.getString(R.string.favorite_deleted_snackbar) to context.getString(
                                            R.string.favorite_undo
                                        )
                                    }
                                    delay(3000)
                                    snackbarState = null
                                }
                            }
                    )
                }
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

            MapLinkButtons(
                onKakaoMapClick = onKakaoMapClick,
                onNaverMapClick = onNaverMapClick,
            )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 104.dp),
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = snackbarState != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 },
                ) {
                    snackbarState?.let { (message, actionLabel) ->
                        EatSsuSnackbar(
                            message = message,
                            actionLabel = actionLabel,
                            onActionClick = {
                                snackbarJob?.cancel()
                                snackbarState = null
                                onLikeClick()
                            },
                            type = EatSsuSnackbarType.Success,
                        )
                    }
            }
        }
    }
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
                isLike = false
            )
        }
    }
}
