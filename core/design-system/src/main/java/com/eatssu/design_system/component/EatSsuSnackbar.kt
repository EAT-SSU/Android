package com.eatssu.design_system.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val action: Color
)

private fun EatSsuSnackbarType.colors(): EatSsuSnackbarColor = when (this) {
    EatSsuSnackbarType.Success -> EatSsuSnackbarColor(
        container = SuccessBg,
        stroke = SuccessBr,
        action = Success
    )

    EatSsuSnackbarType.Danger -> EatSsuSnackbarColor(
        container = DangerBg,
        stroke = DangerBr,
        action = Danger
    )

    EatSsuSnackbarType.Info -> EatSsuSnackbarColor(
        container = InfoBg,
        stroke = InfoBr,
        action = Info
    )

    EatSsuSnackbarType.Warning -> EatSsuSnackbarColor(
        container = WarningBg,
        stroke = WarningBr,
        action = Warning
    )
}

@Composable
fun EatSsuSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    type: EatSsuSnackbarType = EatSsuSnackbarType.Info,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { snackbarData ->
        EatSsuSnackbar(
            snackbarData = snackbarData,
            icon = icon,
            type = type,
        )
    }
}

@Composable
fun EatSsuSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
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
                val actionLabel = snackbarData.visuals.actionLabel
                if (!actionLabel.isNullOrBlank()) {
                    TextButton(onClick = { snackbarData.performAction() }) {
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
                if (icon != null) {
                    androidx.compose.material3.Icon(
                        painter = icon,
                        contentDescription = null,
                    )
                }

                Text(
                    text = snackbarData.visuals.message,
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

private class PreviewSnackbarData(
    message: String,
    actionLabel: String? = null
) : SnackbarData {
    private val previewVisuals = object : SnackbarVisuals {
        override val message: String = message
        override val actionLabel: String? = actionLabel
        override val duration = SnackbarDuration.Short
        override val withDismissAction: Boolean = actionLabel != null
    }

    override val visuals: SnackbarVisuals
        get() = previewVisuals

    override fun dismiss() {}
    override fun performAction() {}
}

@Preview(showBackground = true)
@Composable
private fun EatSsuSnackbarPreview() {
    EatssuTheme {
        EatSsuSnackbar(
            snackbarData = PreviewSnackbarData(
                message = "리뷰가 등록되었어요.",
                actionLabel = "취소"
            ),
            modifier = Modifier.padding(16.dp),
            type = EatSsuSnackbarType.Success
        )
    }
}

