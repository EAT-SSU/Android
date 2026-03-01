package com.eatssu.design_system.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun EatSsuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = (if (fillMaxWidth) modifier.fillMaxWidth() else modifier)
            .height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = Color.White
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier,
            style = EatssuTheme.typography.button1,
        )
    }
}

@ThemePreviews
@Composable
fun PreviewRoundedSelectButton() {
    EatssuTheme {
        EatSsuButton(text = "선택하기", onClick = { /* Button Clicked */ })
    }
}

@ThemePreviews
@Composable
fun PreviewRoundedSelectButtonDisabled() {
    EatssuTheme {
        EatSsuButton(text = "선택하기", onClick = { /* Button Clicked */ }, enabled = false)
    }
}
