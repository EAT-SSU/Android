package com.eatssu.android.presentation.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary

@Composable
fun IntroRoute(
    viewModel: IntroViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onForceUpdate: () -> Unit,
    launchPath: String? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val versionCheck by viewModel.versionCheckResult.collectAsStateWithLifecycle()

    LogScreenView(ScreenId.LOGIN_SPLASH)

    LaunchedEffect(Unit) {
        val path = when (launchPath) {
            "widget" -> LaunchPath.WIDGET
            "local_notification" -> LaunchPath.LOCAL_NOTIFICATION
            "remote_notification" -> LaunchPath.REMOTE_NOTIFICATION
            else -> LaunchPath.ICON
        }
        EventLogger.appLaunch(path)
    }

    LaunchedEffect(versionCheck) {
        when (versionCheck) {
            is VersionCheckResult.ForceUpdateRequired -> onForceUpdate()
            else -> {}
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            IntroUiState.Success -> onNavigateToMain()
            IntroUiState.Error -> onNavigateToLogin()
            else -> {}
        }
    }

    IntroContent()
}

@Composable
private fun IntroContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.img_logo),
            contentDescription = "EAT-SSU Logo",
            modifier = Modifier
                .width(250.dp)
                .padding(65.dp),
            contentScale = ContentScale.Inside,
        )
    }
}

@Preview
@Composable
private fun IntroContentPreview() {
    EatssuTheme {
        IntroContent()
    }
}
