package com.eatssu.android.presentation.mypage.terms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.mypage.MyPageMenuItem
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun TermSelectorScreen(
    modifier: Modifier = Modifier,
    onServiceRuleClick: () -> Unit = {},
    onPrivateInformationClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.terms_and_rule),
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            MyPageMenuItem(
                title = stringResource(R.string.service_rule),
                onClick = onServiceRuleClick,
            )

            MyPageMenuItem(
                title = stringResource(R.string.private_information),
                onClick = onPrivateInformationClick,
            )
        }
    }
}

@Preview
@Composable
fun PreviewTermSelectorScreen(
) {
    EatssuTheme {
        TermSelectorScreen()
    }
}
