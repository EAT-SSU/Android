package com.eatssu.android.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary
import com.eatssu.design_system.theme.White

/**
 * 로그인 화면 Composable
 */
@Composable
fun LoginScreen(
    isLoading: Boolean,
    onKakaoLoginClick: () -> Unit,
    onBrowseGoodPriceStoreClick: () -> Unit, // 착한가격업소 둘러보기 클릭 콜백
    modifier: Modifier = Modifier,
) {
    TrackScreenViewEvent(screenId = ScreenId.LOGIN_LOGIN)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 30.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1.2f))

            // 로고
            Image(
                painter = painterResource(id = R.drawable.img_new_logo_primary),
                contentDescription = "Eat SSU Logo",
                modifier = Modifier.height(75.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 슬로건
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.app_slogan_part1) + " ",
                    style = EatssuTheme.typography.h2,
                    color = Black,
                )
                Text(
                    text = stringResource(id = R.string.app_slogan_part2),
                    style = EatssuTheme.typography.h2,
                    color = Primary,
                )
                val part3 = stringResource(id = R.string.app_slogan_part3)
                if (part3.isNotEmpty()) {
                    Text(
                        text = part3,
                        style = EatssuTheme.typography.h2,
                        color = Black,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.5f))

            // 카카오 로그인 버튼 / 로딩 인디케이터
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Primary,
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_kakao_login_btn),
                        contentDescription = "Kakao Login",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onKakaoLoginClick),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
            )

            // 착한가격업소 둘러보기 버튼 (로그인 없이 바로 지도 화면으로 이동)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        color = Primary,
                        width = 1.dp,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .background(Secondary)
                    .clickable { onBrowseGoodPriceStoreClick() },
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = 14.dp,
                            horizontal = 16.dp,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.ic_map_marker_restaurant),
                        contentDescription = null,
                    )

                    Column {
                        Text(
                            text = stringResource(id = R.string.btn_browse_good_price_stores),
                            style = EatssuTheme.typography.button2,
                            color = Black,
                        )

                        Text(
                            text = stringResource(id = R.string.btn_browse_good_price_stores_subtitle),
                            style = EatssuTheme.typography.caption2,
                            color = Black,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_right),
                        tint = Primary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            // 서울시 로고 영역
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .width(300.dp)
                    .height(20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.seoul_logo_kr),
                        contentDescription = "KOR Seoul Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(89.dp)
                            .height(17.dp),
                    )
                }
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp),
                    color = Gray200,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.seoul_logo_en),
                        contentDescription = "ENG Seoul Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(145.dp)
                            .height(17.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    EatssuTheme {
        LoginScreen(
            isLoading = false,
            onKakaoLoginClick = {},
            onBrowseGoodPriceStoreClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    EatssuTheme {
        LoginScreen(
            isLoading = true,
            onKakaoLoginClick = {},
            onBrowseGoodPriceStoreClick = {},
        )
    }
}
