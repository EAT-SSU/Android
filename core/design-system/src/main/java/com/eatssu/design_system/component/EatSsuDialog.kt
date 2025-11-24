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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.Danger
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EatSsuDialog(
    title: String,
    description: String,
    confirmText: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    onDismissButtonClick: (() -> Unit)? = null,
    icon: Painter? = null,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier,
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
                    if (!dismissText.isNullOrBlank()) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onConfirmClick,
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
    icon: Painter? = null,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier,
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
                        onClick = onConfirmClick,
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
        EatSsuDialog(
            title = "리뷰를 삭제하시겠어요?",
            description = "삭제한 리뷰는 다시 복구할 수 없습니다.",
            confirmText = "확인",
            dismissText = "취소",
            onConfirmClick = {},
            onDismissRequest = {}
        )
    }
}

@Preview
@Composable
private fun EatSsuDialog1Preview() {
    EatssuTheme {
        EatSsuDialog(
            title = "리뷰를 삭제하시겠어요?",
            description = "삭제한 리뷰는 다시 복구할 수 없습니다.",
            confirmText = "확인",
            onConfirmClick = {},
            onDismissRequest = {}
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