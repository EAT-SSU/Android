package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme

@Composable
fun EatssuButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp) //
    ) {
        Text(title)
    }
}

@Preview
@Composable
fun EatssuButtonPreview() {
    EatssuTheme {
        EatssuButton(title = "리뷰 작성하기",
            onClick = {})
    }
}

@Preview
@Composable
fun EatssuButtonPreview2() {
    EatssuTheme {
        EatssuButton(title = "완료하기",
            onClick = {})
    }
}