package com.eatssu.android.presentation.mypage.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.common.enums.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSelectorViewModel @Inject constructor(
    private val settingDataStore: SettingDataStore
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(AppLanguage.SYSTEM)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            settingDataStore.appLanguage.collect { language ->
                _selectedLanguage.value = language
            }
        }
    }

    fun selectLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingDataStore.setAppLanguage(language)
            _selectedLanguage.value = language
            applyLanguage(language)
        }
    }

    private fun applyLanguage(language: AppLanguage) {
        val localeList = if (language == AppLanguage.SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.code)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
