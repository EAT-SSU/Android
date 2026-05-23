package com.eatssu.android.presentation.mypage.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.analytics.LocalAnalyticsTracker
import com.eatssu.common.analytics.ChangeLanguageEvent
import com.eatssu.common.enums.AppLanguage
import com.eatssu.design_system.component.EatSsuRadioCheckBoxGroup
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun LanguageSelectorScreen(
    modifier: Modifier = Modifier,
    viewModel: LanguageSelectorViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val analyticsTracker = LocalAnalyticsTracker.current

    LanguageSelectorContent(
        modifier = modifier,
        selectedLanguage = selectedLanguage,
        onLanguageSelected = { language ->
            if (selectedLanguage != language) {
                analyticsTracker.track(
                    ChangeLanguageEvent(
                        lang_from = selectedLanguage.code,
                        lang_to = language.code,
                    ),
                )
            }
            viewModel.selectLanguage(language)
        },
        onBack = onBack
    )
}

@Composable
fun LanguageSelectorContent(
    modifier: Modifier = Modifier,
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onBack: () -> Unit = {}
) {
    val languageOptions = AppLanguage.entries.map { language ->
        language.nativeDisplayName
    }

    val selectedOption = selectedLanguage.nativeDisplayName

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.language_setting),
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
//            Text(
//                text = stringResource(R.string.language_select_description),
//                style = EatssuTheme.typography.body2,
//                modifier = Modifier.padding(vertical = 20.dp)
//            )

            EatSsuRadioCheckBoxGroup(
                options = languageOptions,
                selectedOption = selectedOption,
                onOptionSelected = { selected ->
                    val language = AppLanguage.entries.find { lang ->
                        lang.nativeDisplayName == selected
                    } ?: AppLanguage.KOREAN
                    onLanguageSelected(language)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectorScreenPreview() {
    EatssuTheme {
        LanguageSelectorContent(
            selectedLanguage = AppLanguage.KOREAN,
            onLanguageSelected = {},
            onBack = {}
        )
    }
}
