package com.eatssu.design_system.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.eatssu.design_system.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray500

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EatSsuTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                style = EatssuTheme.typography.subtitle1
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "뒤로가기",
                    tint = Gray500
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    EatssuTheme {
        Column {
            EatSsuTopBar("리뷰", {})
        }
    }
}