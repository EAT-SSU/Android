package com.eatssu.android.presentation.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.common.UiEvent

import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.component.EatssuTextField
import com.eatssu.design_system.component.EatssuToastHost
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray600

@Composable
fun SignOutRoute(
    viewModel: SignOutViewModel = hiltViewModel(),
    nickname: String,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var toastType by remember { mutableStateOf(ToastType.INFO) }
    var inputNickname by remember { mutableStateOf("") }

    LogScreenView(ScreenId.MYPAGE_SIGNOUT)

    LaunchedEffect(uiState) {
        if (uiState is SignOutUiState.SignedOut) {
            onNavigateToLogin()
        }
    }

    ObserveUiEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> {
                toastType = event.type
                snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    SignOutContent(
        nickname = nickname,
        inputNickname = inputNickname,
        onInputNicknameChange = { inputNickname = it },
        onBack = onBack,
        onSignOut = { viewModel.signOut() },
        signOutEnabled = inputNickname.trim() == nickname,
        snackbarHostState = snackbarHostState,
        toastType = toastType,
    )
}

@Composable
private fun SignOutContent(
    nickname: String,
    inputNickname: String,
    onInputNicknameChange: (String) -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    signOutEnabled: Boolean,
    snackbarHostState: SnackbarHostState,
    toastType: ToastType,
) {
    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.title_sign_out),
                onBack = onBack,
            )
        },
        snackbarHost = {
            EatssuToastHost(
                hostState = snackbarHostState,
                toastType = toastType,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
            ) {
                Text(
                    text = stringResource(R.string.signout_question),
                    style = EatssuTheme.typography.subtitle1,
                    color = Color.Black,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.signout_description),
                    style = EatssuTheme.typography.caption2,
                    color = Gray600,
                )
                Spacer(Modifier.height(16.dp))
                EatssuTextField(
                    value = inputNickname,
                    onValueChange = onInputNicknameChange,
                    hint = nickname,
                    maxLength = 8,
                    singleLine = true,
                )
            }

            EatSsuButton(
                text = stringResource(R.string.signout),
                onClick = onSignOut,
                enabled = signOutEnabled,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SignOutContentPreview() {
    EatssuTheme {
        SignOutContent(
            nickname = "잇쑤",
            inputNickname = "잇쑤",
            onInputNicknameChange = {},
            onBack = {},
            onSignOut = {},
            signOutEnabled = true,
            snackbarHostState = remember { SnackbarHostState() },
            toastType = ToastType.INFO,
        )
    }
}
