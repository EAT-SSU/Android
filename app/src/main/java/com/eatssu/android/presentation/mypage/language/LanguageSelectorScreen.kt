package com.eatssu.android.presentation.mypage.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.common.enums.AppLanguage
import com.eatssu.design_system.component.EatSsuRadioButtonGroup
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun LanguageSelectorScreen(
    modifier: Modifier = Modifier,
    viewModel: LanguageSelectorViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    LanguageSelectorContent(
        modifier = modifier,
        selectedLanguage = selectedLanguage,
        onLanguageSelected = { viewModel.selectLanguage(it) },
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
        when (language) {
            AppLanguage.SYSTEM -> stringResource(R.string.language_system_default)
            else -> language.nativeDisplayName
        }
    }

    val selectedOption = when (selectedLanguage) {
        AppLanguage.SYSTEM -> stringResource(R.string.language_system_default)
        else -> selectedLanguage.nativeDisplayName
    }

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
            Text(
                text = stringResource(R.string.language_select_description),
                style = EatssuTheme.typography.body2,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            EatSsuRadioButtonGroup(
                options = languageOptions,
                selectedOption = selectedOption,
                onOptionSelected = { selected ->
                    val language = AppLanguage.entries.find { lang ->
                        if (lang == AppLanguage.SYSTEM) {
                            selected == languageOptions.first()
                        } else {
                            lang.nativeDisplayName == selected
                        }
                    } ?: AppLanguage.SYSTEM
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
