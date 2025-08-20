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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun EatSsuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier,
//                .padding(vertical = 13.dp),
            style = EatssuTheme.typography.button1,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoundedSelectButton() {

    EatssuTheme {
        EatSsuButton(text = "선택하기", onClick = { /* Button Clicked */ })
    }
}
