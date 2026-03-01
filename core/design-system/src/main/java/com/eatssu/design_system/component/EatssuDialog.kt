package com.eatssu.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Error
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary

@Composable
fun EatssuDialog(
    title: String,
    description: String,
    confirmText: String = "확인",
    cancelText: String = "취소",
    showCancelButton: Boolean = true,
    isDestructive: Boolean = false,
    cancellable: Boolean = true,
    onConfirm: () -> Unit,
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = {
            if (cancellable) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = cancellable,
            dismissOnClickOutside = cancellable,
        )
    ) {
        // 전체 너비 321.dp (dialog_default.xml 기준)
        Column(
            modifier = Modifier
                .width(321.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp) // shape_dialog_corner
                )
                .padding(18.dp), // shape_dialog_corner padding
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Title: H2 style, bold, black
            Text(
                text = title,
                style = EatssuTheme.typography.h2,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            // Description: Subtitle2 style, gray600, marginTop=8dp
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                style = EatssuTheme.typography.subtitle2,
                color = Gray600,
            )
            // Buttons: marginTop=18dp
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isDestructive) {
                    // 파괴형: Confirm(Error) 먼저, Cancel(Gray200) 뒤
                    Button(
                        onClick = { onConfirm(); onDismiss() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Error // #FF3F3F
                        ),
                    ) {
                        Text(confirmText, style = EatssuTheme.typography.body1, color = Color.White)
                    }
                    if (showCancelButton) {
                        Button(
                            onClick = { onCancel(); onDismiss() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gray200
                            ),
                        ) {
                            Text(
                                cancelText,
                                style = EatssuTheme.typography.body1,
                                color = Color.Black
                            )
                        }
                    }
                } else {
                    // 기본형: Cancel(Gray200) 먼저, Confirm(Primary) 뒤
                    if (showCancelButton) {
                        Button(
                            onClick = { onCancel(); onDismiss() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gray200
                            ),
                        ) {
                            Text(
                                cancelText,
                                style = EatssuTheme.typography.body1,
                                color = Color.Black
                            )
                        }
                    }
                    Button(
                        onClick = { onConfirm(); onDismiss() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary // #66D4C2
                        ),
                    ) {
                        Text(confirmText, style = EatssuTheme.typography.body1, color = Color.White)
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun EatssuDialogPreview() {
    EatssuTheme {
        EatssuDialog(
            title = "로그아웃",
            description = "정말 로그아웃 하시겠어요?",
            onConfirm = {},
            onCancel = {},
        )
    }
}
