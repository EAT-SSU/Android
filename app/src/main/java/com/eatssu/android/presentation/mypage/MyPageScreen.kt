package com.eatssu.android.presentation.mypage

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.White

@Composable
fun MyPageScreen(
    state: MyPageState,
    departmentName: String,
    onAlarmToggle: (Boolean) -> Unit,
    onMyInfoClick: () -> Unit,
    onMyReviewClick: () -> Unit,
    onInquireClick: () -> Unit,
    onInstagramClick: () -> Unit,
    onLanguageSettingClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onOssClick: () -> Unit,
    onTermRulesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAppVersionClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(bottom = dimensionResource(id = R.dimen.bottom_nav_height)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 17.dp)
                .height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.mypage),
                style = EatssuTheme.typography.subtitle1,
                color = Gray700,
                textAlign = TextAlign.Center,
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(48.dp),
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = stringResource(R.string.mypage),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
            ) {
                Column {
                    Text(
                        text = state.nickname ?: stringResource(R.string.set_nickname),
                        style = EatssuTheme.typography.subtitle2,
                        color = Gray700,
                    )

                    Text(
                        text = departmentName,
                        style = EatssuTheme.typography.body3,
                        color = Gray700,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        MyPageSectionText(
            text = stringResource(R.string.mypage_push_activity_section_title)
        )

        NotificationSettingRow(
            checked = state.isAlarmOn,
            onCheckedChange = onAlarmToggle,
        )

        MyPageMenuItem(
            title = stringResource(R.string.my_info),
            onClick = onMyInfoClick,
        )
        MyPageMenuItem(
            title = stringResource(R.string.my_review),
            onClick = onMyReviewClick,
        )

        MyPageDivider()

        MyPageSectionText(
            text = stringResource(R.string.mypage_service_section_title)
        )

        MyPageMenuItem(
            title = stringResource(R.string.inquire),
            onClick = onInquireClick,
        )

        MyPageMenuItem(
            title = stringResource(R.string.developer),
            onClick = onDeveloperClick,
        )

        MyPageMenuItem(
            title = stringResource(R.string.eatssu_instagram_link),
            onClick = onInstagramClick,
        )

        MyPageDivider()

        MyPageSectionText(
            text = stringResource(R.string.mypage_etc_section_title)
        )

        MyPageMenuItem(
            title = stringResource(R.string.language_setting),
            onClick = onLanguageSettingClick,
        )

        MyPageMenuItem(
            title = stringResource(R.string.terms_and_rule),
            onClick = onTermRulesClick,
        )

//        MyPageMenuItem(
//            title = stringResource(R.string.oss_licenses),
//            onClick = onOssClick,
//        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogoutClick)
                .padding(horizontal = 24.dp, vertical = 18.dp),
            text = stringResource(R.string.logout),
            style = EatssuTheme.typography.body1,
            color = Gray700,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAppVersionClick)
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_version),
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
            Text(
                text = state.appVersion,
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onSignOutClick)
                .padding(start = 24.dp, top = 10.dp, end = 24.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.signout),
                style = EatssuTheme.typography.caption2,
                color = Gray400,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_unsubscribe_16),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun MyPageDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Gray300),
    )
}

@Composable
private fun NotificationSettingRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.mypage_push_notification_title),
                    style = EatssuTheme.typography.body1,
                    color = Gray700,
                )
                Text(
                    text = stringResource(R.string.mypage_push_notification_description),
                    style = EatssuTheme.typography.caption2,
                    color = Gray400,
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
fun MyPageSectionText(text: String) {
    Text(
        modifier = Modifier
            .padding(start = 24.dp, top = 10.dp),
        text = text,
        style = EatssuTheme.typography.caption1,
        color = Gray500
    )
}

@Composable
fun MyPageMenuItem(
    title: String,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = EatssuTheme.typography.body1,
                color = Gray700,
            )
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = Gray300,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    EatssuTheme {
        MyPageScreen(
            state = MyPageState(
                nickname = "hellosoongsil1234",
                isAlarmOn = true,
                appVersion = "1.0.0 (1)",
            ),
            departmentName = "컴퓨터학부",
            onAlarmToggle = {},
            onMyInfoClick = {},
            onMyReviewClick = {},
            onInquireClick = {},
            onInstagramClick = {},
            onTermRulesClick = {},
            onDeveloperClick = {},
            onOssClick = {},
            onLogoutClick = {},
            onAppVersionClick = {},
            onLanguageSettingClick = {},
            onSignOutClick = {},
        )
    }
}
