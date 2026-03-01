package com.eatssu.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray600

/**
 * Toast 타입별 배경색/테두리색 매핑 (shape_toast_*.xml 기반)
 */
private fun ToastType.backgroundColor(): Color = when (this) {
    ToastType.INFO -> Color(0xFFE7F4FE)
    ToastType.SUCCESS -> Color(0xFFEAF6EC)
    ToastType.WARNING -> Color(0xFFFFF3DB)
    ToastType.ERROR -> Color(0xFFFDEFEC)
}

private fun ToastType.borderColor(): Color = when (this) {
    ToastType.INFO -> Color(0xFFD3EBFD)
    ToastType.SUCCESS -> Color(0xFFD8EEDD)
    ToastType.WARNING -> Color(0xFFFFE0A3)
    ToastType.ERROR -> Color(0xFFFCDFD9)
}

@Composable
fun EatssuToastHost(
    hostState: SnackbarHostState,
    toastType: ToastType,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .padding(horizontal = 16.dp) // 좌우 16dp
            .padding(bottom = 84.dp),    // 하단 84dp (BottomNav 위)
    ) { snackbarData ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 60.dp) // minHeight=60dp
                .background(
                    color = toastType.backgroundColor(),
                    shape = RoundedCornerShape(12.dp),
                )
                .border(
                    width = 1.dp,
                    color = toastType.borderColor(),
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 아이콘: 24×24dp, marginEnd=8dp
            Icon(
                painter = painterResource(id = toastType.iconId),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified,
            )
            Spacer(Modifier.width(8.dp))
            // 텍스트: Body1, gray600, maxLines=2
            Text(
                text = snackbarData.visuals.message,
                style = EatssuTheme.typography.body1,
                color = Gray600,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview
@Composable
private fun EatssuToastHostPreview() {
    EatssuTheme {
        val hostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            hostState.showSnackbar("알림이 설정되었습니다")
        }

        EatssuToastHost(
            hostState = hostState,
            toastType = ToastType.SUCCESS,
        )
    }
}
