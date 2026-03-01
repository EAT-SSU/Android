package com.eatssu.design_system.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.eatssu.design_system.preview.ThemePreviews
import com.eatssu.design_system.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloseTopBar(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = EatssuTheme.typography.subtitle1
            )
        },
        actions = {
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "닫기",
                    tint = Gray500
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@ThemePreviews
@Composable
fun TopBarPreview() {
    EatssuTheme {
        Column {
            EatSsuTopBar("리뷰", {})
        }
    }
}

@ThemePreviews

@Composable
private fun CloseTopBarPreview() {
    EatssuTheme {
        CloseTopBar(title = "가운데 제목", {})
    }
}

