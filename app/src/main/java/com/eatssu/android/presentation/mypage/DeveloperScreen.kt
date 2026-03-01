package com.eatssu.android.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.android.presentation.util.debouncedClickable
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme

private val GradientStart = Color(0xFFB8E4FF)
private val GradientCenter = Color(0xFFCAF2FB)
private val GradientEnd = Color(0xFFC7FFE3)

@Composable
fun DeveloperScreen(
    onBack: () -> Unit,
    onRecruitingClick: () -> Unit,
) {
    LogScreenView(ScreenId.MYPAGE_DEVELOPER)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(GradientStart, GradientCenter, GradientEnd),
    )

    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.developer),
                onBack = onBack,
                containerColor = Color.Transparent,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_instagram),
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 6.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.eatssu_instagram),
                    style = EatssuTheme.typography.subtitle2,
                    color = Color.Black,
                )
            }

            Image(
                painter = painterResource(R.drawable.img_new_member_link),
                contentDescription = "Recruiting",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 34.dp)
                    .debouncedClickable { onRecruitingClick() },
                contentScale = ContentScale.FillWidth,
            )

            Image(
                painter = painterResource(R.drawable.img_developer),
                contentDescription = "Developers",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 42.dp)
                    .padding(top = 30.dp, bottom = 24.dp),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

@Preview
@Composable
private fun DeveloperScreenPreview() {
    EatssuTheme {
        DeveloperScreen(
            onBack = {},
            onRecruitingClick = {},
        )
    }
}
