package com.eatssu.android.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White

@Composable
fun LoginScreen(
    isLoading: Boolean,
    onKakaoLoginClick: () -> Unit,
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
                    text = stringResource(id = R.string.app_slogan_part1),
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
        )
    }
}
