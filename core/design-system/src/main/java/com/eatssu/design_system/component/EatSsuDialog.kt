package com.eatssu.design_system.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.Danger
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.White
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun EatSsuDialog(
    visible: Boolean,
    title: String,
    description: String,
    confirmText: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    dismissText: String? = null,
    onDismissButtonClick: (() -> Unit)? = null,
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val view = LocalView.current
        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.let { window ->
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                window.setDimAmount(0f)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(dampingRatio = 0.7f)
                ),
                exit = fadeOut() + scaleOut()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = White
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                style = EatssuTheme.typography.h2,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = description,
                                style = EatssuTheme.typography.subtitle2,
                                color = Gray600
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!dismissText.isNullOrBlank()) {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        onDismissButtonClick?.invoke()
                                            ?: onDismissRequest()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gray200,
                                        contentColor = Black
                                    )
                                ) {
                                    Text(
                                        text = dismissText,
                                        style = EatssuTheme.typography.button2
                                    )
                                }
                            }

                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = onConfirmClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = confirmText,
                                    style = EatssuTheme.typography.button2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EatSsuWarningDialog(
    title: String,
    description: String,
    confirmText: String,
    dismissText: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissButtonClick: (() -> Unit)? = null,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = EatssuTheme.typography.h2,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = EatssuTheme.typography.subtitle2,
                        color = Gray600
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onConfirmClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Danger,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = confirmText,
                            style = EatssuTheme.typography.button2
                        )
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onDismissButtonClick?.invoke() ?: onDismissRequest() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray200,
                            contentColor = Black
                        )
                    ) {
                        Text(
                            text = dismissText,
                            style = EatssuTheme.typography.button2
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EatSsuDialogPreview() {
    EatssuTheme {
        var showDialog by remember { mutableStateOf(false) }

        EatSsuDialog(
            visible = showDialog,
            title = "나가시겠어요?",
            description = "지금 나가면 작성한 내용이 저장되지 않습니다.",
            confirmText = "계속 작성",
            dismissText = "나가기",
            onConfirmClick = { showDialog = false },
            onDismissRequest = { showDialog = false }
        )
    }
}

@Preview
@Composable
private fun EatSsuDialog1Preview() {
    EatssuTheme {
        var showDialog by remember { mutableStateOf(false) }

        EatSsuDialog(
            visible = showDialog,
            title = "리뷰를 삭제하시겠어요?",
            description = "삭제한 리뷰는 다시 복구할 수 없습니다.",
            confirmText = "확인",
            onConfirmClick = { showDialog = false },
            onDismissRequest = { showDialog = false }
        )
    }
}

@Preview
@Composable
private fun EatSsuDangerDialogPreview() {
    EatssuTheme {
        EatSsuWarningDialog(
            title = "리뷰를 삭제하시겠어요?",
            description = "삭제한 리뷰는 다시 복구할 수 없습니다.",
            confirmText = "확인",
            dismissText = "취소",
            onConfirmClick = {},
            onDismissRequest = {}
        )
    }
}
