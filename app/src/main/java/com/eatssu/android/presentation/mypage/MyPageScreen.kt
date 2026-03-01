package com.eatssu.android.presentation.mypage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.R
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.common.UiEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.component.EatssuDialog
import com.eatssu.design_system.component.EatssuToastHost
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@Composable
fun MyPageRoute(
    myPageViewModel: MyPageViewModel = hiltViewModel(),
    onNavigateToUserInfo: (force: Boolean) -> Unit,
    onNavigateToMyReview: () -> Unit,
    onNavigateToInquire: () -> Unit,
    onNavigateToWebView: (url: String, title: String, screenId: ScreenId) -> Unit,
    onLogout: () -> Unit,
    onNavigateToSignOut: (nickname: String?) -> Unit,
    onNavigateToDeveloper: () -> Unit,
    onNavigateToOss: () -> Unit,
) {
    val uiState by myPageViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var toastType by remember { mutableStateOf(ToastType.INFO) }
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    LogScreenView(ScreenId.MYPAGE_MAIN)

    // onResume에서 fetchMyInfo 호출 (닉네임 변경 등으로부터 복귀 시 정보 갱신)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            myPageViewModel.fetchMyInfo()
        }
    }

    ObserveUiEvents(myPageViewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> {
                toastType = event.type
                snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    if (showLogoutDialog) {
        EatssuDialog(
            title = stringResource(R.string.dialog_logout_title),
            description = stringResource(R.string.dialog_logout_message),
            isDestructive = true,
            onConfirm = {
                onLogout()
                showLogoutDialog = false
            },
            onCancel = { showLogoutDialog = false },
            onDismiss = { showLogoutDialog = false },
        )
    }

    if (showNotificationPermissionDialog) {
        EatssuDialog(
            title = stringResource(R.string.dialog_notification_permission_title),
            description = stringResource(R.string.dialog_notification_permission_description),
            confirmText = stringResource(R.string.dialog_settings),
            cancelText = stringResource(R.string.button_cancel),
            onConfirm = {
                openAppNotificationSettings(context)
                showNotificationPermissionDialog = false
            },
            onCancel = { showNotificationPermissionDialog = false },
            onDismiss = { showNotificationPermissionDialog = false },
        )
    }

    val state = uiState

    Scaffold(
        snackbarHost = {
            EatssuToastHost(
                hostState = snackbarHostState,
                toastType = toastType,
            )
        },
    ) { innerPadding ->
        MyPageScreenContent(
            state = state,
            onNotificationToggle = { checked ->
                val nowDatetime = LocalDateTime.now()
                val formattedDate = nowDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

                if (checked) {
                    if (checkNotificationPermission(context)) {
                        myPageViewModel.setNotificationOn()
                        scope.launch {
                            toastType = ToastType.INFO
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.toast_notification_enable, formattedDate)
                            )
                        }
                    } else {
                        showNotificationPermissionDialog = true
                    }
                } else {
                    myPageViewModel.setNotificationOff()
                    scope.launch {
                        toastType = ToastType.INFO
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.toast_notification_disable, formattedDate)
                        )
                    }
                }
            },
            onMyInfoClick = { onNavigateToUserInfo(false) },
            onMyReviewClick = onNavigateToMyReview,
            onInquireClick = onNavigateToInquire,
            onServiceRuleClick = {
                onNavigateToWebView(
                    context.getString(R.string.terms_url),
                    context.getString(R.string.terms),
                    ScreenId.EXTERNAL_TERMS,
                )
            },
            onPrivateInformationClick = {
                onNavigateToWebView(
                    context.getString(R.string.policy_url),
                    context.getString(R.string.policy),
                    ScreenId.EXTERNAL_POLICY,
                )
            },
            onDeveloperClick = onNavigateToDeveloper,
            onOssClick = onNavigateToOss,
            onLogoutClick = { showLogoutDialog = true },
            onSignOutClick = { onNavigateToSignOut(state.nickname) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
internal fun MyPageScreenContent(
    state: MyPageState,
    onNotificationToggle: (Boolean) -> Unit,
    onMyInfoClick: () -> Unit,
    onMyReviewClick: () -> Unit,
    onInquireClick: () -> Unit,
    onServiceRuleClick: () -> Unit,
    onPrivateInformationClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onOssClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(17.dp))
        Text(
            text = stringResource(R.string.mypage),
            style = EatssuTheme.typography.subtitle1,
            color = Gray700,
        )

        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = "Profile",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = if (state.hasNickname) {
                state.nickname.orEmpty()
            } else {
                stringResource(R.string.set_nickname)
            },
            style = EatssuTheme.typography.body1,
            color = Color.Black,
            modifier = Modifier.padding(8.dp),
        )

        Spacer(Modifier.height(41.dp))
        MyPageNotificationRow(
            isChecked = state.isAlarmOn,
            onCheckedChange = onNotificationToggle,
        )

        MyPageMenuItem(stringResource(R.string.my_info), onMyInfoClick)
        MyPageMenuItem(stringResource(R.string.my_review), onMyReviewClick)
        MyPageMenuItem(stringResource(R.string.inquire), onInquireClick)
        MyPageMenuItem(stringResource(R.string.service_rule), onServiceRuleClick)
        MyPageMenuItem(stringResource(R.string.private_information), onPrivateInformationClick)
        MyPageMenuItem(stringResource(R.string.developer), onDeveloperClick)
        MyPageMenuItem(stringResource(R.string.oss_licenses), onOssClick)

        Text(
            text = stringResource(R.string.logout),
            style = EatssuTheme.typography.body1,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogoutClick)
                .padding(horizontal = 24.dp, vertical = 18.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
        ) {
            Text(
                text = stringResource(R.string.app_version),
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = state.appVersion,
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSignOutClick)
                .padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.signout),
                style = EatssuTheme.typography.caption2,
                color = Gray400,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_unsubscribe_16),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Gray400,
            )
        }
    }
}

@Composable
private fun MyPageMenuItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = EatssuTheme.typography.body1, color = Color.Black)
        Spacer(Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Gray300,
        )
    }
}

@Composable
private fun MyPageNotificationRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.mypage_push_notification_title),
                style = EatssuTheme.typography.body1,
                color = Color.Black,
            )
            Text(
                text = stringResource(R.string.mypage_push_notification_description),
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Primary,
                uncheckedTrackColor = Gray400,
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
            ),
        )
    }
}

private fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun openAppNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

@Preview
@Composable
private fun MyPageMenuItemPreview() {
    EatssuTheme {
        MyPageMenuItem(
            title = "내 정보",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun MyPageNotificationRowPreview() {
    EatssuTheme {
        MyPageNotificationRow(
            isChecked = true,
            onCheckedChange = {},
        )
    }
}
