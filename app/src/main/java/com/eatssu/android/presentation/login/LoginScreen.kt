package com.eatssu.android.presentation.login

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.android.presentation.util.debouncedClickable
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onBackPress: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            LoginUiState.Success -> onNavigateToMain()
            else -> {}
        }
    }

    ObserveUiEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> context.showToast(event)
        }
    }

    LoginContent(
        uiState = uiState,
        onBackPress = onBackPress,
        onLoginClick = { handleKakaoLogin(context, viewModel) },
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onBackPress: () -> Unit,
    onLoginClick: () -> Unit,
) {
    BackHandler { onBackPress() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
    ) {
        Image(
            painter = painterResource(R.drawable.img_new_logo_primary),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 65.dp)
                .padding(top = 300.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Fit,
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 375.dp + 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Text("숭실대에서 ", style = EatssuTheme.typography.h2, color = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text("먹자", style = EatssuTheme.typography.h2, color = Primary)
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.img_kakao_login_btn),
                    contentDescription = "카카오 로그인",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .debouncedClickable { onLoginClick() },
                    contentScale = ContentScale.Inside,
                )
                if (uiState == LoginUiState.Loading) {
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginContentPreview() {
    EatssuTheme {
        LoginContent(
            uiState = LoginUiState.Idle,
            onBackPress = {},
            onLoginClick = {},
        )
    }
}

// Kakao 로그인은 Activity context 필요하므로 top-level 함수로 유지
private fun handleKakaoLogin(context: Context, viewModel: LoginViewModel) {
    viewModel.setLoadingState()

    CoroutineScope(Dispatchers.Main).launch {
        try {
            val token = UserApiClient.loginWithKakao(context)
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Timber.e(error, "사용자 정보 요청 실패")
                    viewModel.setInitState()
                } else if (user != null) {
                    val email = user.kakaoAccount?.email ?: ""
                    val providerID = user.id.toString()
                    viewModel.getKakaoLogin(email, providerID)
                }
            }
        } catch (e: Exception) {
            if (e is ClientError && e.reason == ClientErrorCause.Cancelled) {
                viewModel.setInitState()
            } else {
                Timber.e(e, "로그인 실패")
                viewModel.setInitState()
            }
        }
    }
}
