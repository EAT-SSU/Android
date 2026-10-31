package com.eatssu.android.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.common.enums.Time
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary
import com.eatssu.design_system.theme.White
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@Composable
internal fun NotificationScreen(
    notifications: List<MenuNotification>?,
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (MenuNotification) -> Unit,
) {
    val now by produceState(initialValue = Instant.now()) {
        while (true) {
            delay(60_000)
            value = Instant.now()
        }
    }
    Box(Modifier
        .fillMaxSize()
        .background(White)
        .systemBarsPadding()) {
        if (notifications?.isEmpty() == true) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    stringResource(R.string.notification_empty_title),
                    style = EatssuTheme.typography.subtitle2,
                    color = Gray600,
                )
                Text(
                    stringResource(R.string.notification_empty_description),
                    style = EatssuTheme.typography.caption2,
                    color = Gray600,
                )
            }
        }
        Column(Modifier.fillMaxSize()) {
            // Insets are owned by the screen rather than the shared top app bar.
            EatSsuTopBar(
                title = stringResource(R.string.notification_title),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            when {
                notifications == null -> Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }

                notifications.isNotEmpty() -> {
                    val hasUnread = notifications.any { !it.isRead }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 24.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            stringResource(R.string.notification_mark_all_read),
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    enabled = hasUnread,
                                    onClick = onMarkAllRead,
                                )
                                .padding(vertical = 8.dp),
                            style = EatssuTheme.typography.button2,
                            color = if (hasUnread) Gray600 else Gray600.copy(alpha = 0.2f),
                        )
                    }
                    LazyColumn(Modifier.weight(1f)) {
                        items(notifications, key = { it.id }) { notification ->
                            NotificationRow(notification, now, onNotificationClick)
                        }
                    }
                    Text(
                        stringResource(R.string.notification_retention),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp, bottom = 28.dp),
                        style = EatssuTheme.typography.caption2,
                        color = Gray500,
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: MenuNotification,
    now: Instant,
    onClick: (MenuNotification) -> Unit
) {
    val received = Instant.ofEpochMilli(notification.receivedAt)
    val hours = Duration.between(received, now).toHours().coerceAtLeast(0)
    val date = received.atZone(ZoneId.systemDefault()).toLocalDate()
    val time = if (hours < 24) stringResource(R.string.notification_hours_ago, hours)
    else stringResource(R.string.notification_date, date.monthValue, date.dayOfMonth)
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (notification.isRead) White else Secondary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick(notification) },
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(36.dp)
                .background(White, CircleShape)
                .border(1.dp, Primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(R.drawable.ic_notification_speaker),
                null,
                Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    notification.title,
                    Modifier.weight(1f),
                    style = EatssuTheme.typography.subtitle1,
                    color = Gray700
                )
                Text(time, style = EatssuTheme.typography.caption2, color = Gray600)
            }
            Text(notification.body, style = EatssuTheme.typography.body3, color = Gray600)
        }
    }
}

@Preview(widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun EmptyPreview() = EatssuTheme { NotificationScreen(emptyList(), {}, {}, {}) }

private fun previewNotifications() = listOf(
    MenuNotification(
        "1",
        "🍽️ 찜한 메뉴가 나온대요!",
        "오늘 점심, 학생식당에 파스타 등장 👀",
        System.currentTimeMillis() - 3_600_000,
        "2026-10-31",
        Time.LUNCH
    ),
    MenuNotification(
        "2",
        "🍽️ 찜한 메뉴가 2개나 나온대요!",
        "오늘 점심, 도담식당 파스타 · 학생식당 파스타 등장 👀",
        System.currentTimeMillis() - 86_400_000,
        "2026-10-30",
        Time.LUNCH,
        true
    ),
    MenuNotification(
        "3",
        "🍽️ 찜한 메뉴가 여러 개 나온대요!",
        "오늘 점심, 도담식당 파스타 외 2개 등장 👀",
        System.currentTimeMillis() - 172_800_000,
        "2026-10-29",
        Time.LUNCH,
        true
    ),
)

@Preview(widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun UnreadPreview() = EatssuTheme { NotificationScreen(previewNotifications(), {}, {}, {}) }

@Preview(widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun ReadPreview() = EatssuTheme {
    NotificationScreen(previewNotifications().map { it.copy(isRead = true) }, {}, {}, {})
}
