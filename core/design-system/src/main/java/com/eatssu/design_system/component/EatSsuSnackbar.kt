package com.eatssu.design_system.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.common.R
import com.eatssu.design_system.theme.Danger
import com.eatssu.design_system.theme.DangerBg
import com.eatssu.design_system.theme.DangerBr
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Info
import com.eatssu.design_system.theme.InfoBg
import com.eatssu.design_system.theme.InfoBr
import com.eatssu.design_system.theme.Success
import com.eatssu.design_system.theme.SuccessBg
import com.eatssu.design_system.theme.SuccessBr
import com.eatssu.design_system.theme.Warning
import com.eatssu.design_system.theme.WarningBg
import com.eatssu.design_system.theme.WarningBr

enum class EatSsuSnackbarType {
    Success, Danger, Info, Warning
}

private data class EatSsuSnackbarColor(
    val container: Color,
    val stroke: Color,
    val action: Color,
    val iconRes: Int
)

private fun EatSsuSnackbarType.colors(): EatSsuSnackbarColor = when (this) {
    EatSsuSnackbarType.Success -> EatSsuSnackbarColor(
        container = SuccessBg,
        stroke = SuccessBr,
        action = Success,
        iconRes = R.drawable.ic_toast_success
    )

    EatSsuSnackbarType.Danger -> EatSsuSnackbarColor(
        container = DangerBg,
        stroke = DangerBr,
        action = Danger,
        iconRes = R.drawable.ic_toast_error
    )

    EatSsuSnackbarType.Info -> EatSsuSnackbarColor(
        container = InfoBg,
        stroke = InfoBr,
        action = Info,
        iconRes = R.drawable.ic_toast_info
    )

    EatSsuSnackbarType.Warning -> EatSsuSnackbarColor(
        container = WarningBg,
        stroke = WarningBr,
        action = Warning,
        iconRes = R.drawable.ic_toast_warning
    )
}

@Composable
fun EatSsuSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    type: EatSsuSnackbarType = EatSsuSnackbarType.Info,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { snackbarData ->
        EatSsuSnackbar(
            message = snackbarData.visuals.message,
            actionLabel = snackbarData.visuals.actionLabel,
            onActionClick = { snackbarData.performAction() },
            type = type,
        )
    }
}

@Composable
fun EatSsuSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    type: EatSsuSnackbarType = EatSsuSnackbarType.Info,
) {
    val colors = type.colors()

    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.stroke),
        color = Color.Transparent,
        modifier = modifier.padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Snackbar(
            containerColor = colors.container,
            shape = RoundedCornerShape(12.dp),
            action = {
                if (!actionLabel.isNullOrBlank()) {
                    TextButton(onClick = { onActionClick?.invoke() }) {
                        Text(
                            text = actionLabel,
                            style = EatssuTheme.typography.button2.copy(
                                textDecoration = TextDecoration.Underline
                            ),
                            color = colors.action
                        )
                    }
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    painter = painterResource(id = colors.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )

                Text(
                    text = message,
                    modifier = Modifier.weight(1f),
                    style = EatssuTheme.typography.body1,
                    color = Gray600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EatSsuSuccessSnackbarPreview() {
    EatssuTheme {
        EatSsuSnackbar(
            message = "리뷰가 등록되었어요.",
            actionLabel = "취소",
            modifier = Modifier.padding(16.dp),
            type = EatSsuSnackbarType.Success
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EatSsuDangerSnackbarPreview() {
    EatssuTheme {
        EatSsuSnackbar(
            message = "리뷰 삭제 중 오류가 발생했어요.",
            actionLabel = "재시도",
            modifier = Modifier.padding(16.dp),
            type = EatSsuSnackbarType.Danger
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EatSsuInfoSnackbarPreview() {
    EatssuTheme {
        EatSsuSnackbar(
            message = "새로운 업데이트가 있습니다.",
            modifier = Modifier.padding(16.dp),
            type = EatSsuSnackbarType.Info
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EatSsuWarningSnackbarPreview() {
    EatssuTheme {
        EatSsuSnackbar(
            message = "네트워크 연결이 불안정합니다.네트워크 연결이 불안정합니다.네트워크 연결이 불안정합니다.",
            modifier = Modifier.padding(16.dp),
            type = EatSsuSnackbarType.Warning
        )
    }
}
