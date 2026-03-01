package com.eatssu.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Error
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400

@Composable
fun EatssuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    isError: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .background(
                color = Gray100,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = if (isError) Error else Gray200,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 11.dp, vertical = 16.dp),
        textStyle = EatssuTheme.typography.body2.copy(color = Color.Black),
        maxLines = maxLines,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = EatssuTheme.typography.body2,
                        color = Gray400,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview
@Composable
private fun EatssuTextFieldPreview() {
    var value by remember { mutableStateOf("") }

    EatssuTheme {
        EatssuTextField(
            value = value,
            onValueChange = { value = it },
            hint = "닉네임을 입력하세요",
        )
    }
}
