package com.eatssu.android.presentation.mypage.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.common.enums.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSelectorViewModel @Inject constructor(
    private val settingDataStore: SettingDataStore,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(AppLanguage.KOREAN)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _languageChanged = MutableSharedFlow<Unit>()
    val languageChanged: SharedFlow<Unit> = _languageChanged.asSharedFlow()

    init {
        viewModelScope.launch {
            settingDataStore.appLanguage.collect { language ->
                _selectedLanguage.value = language
            }
        }
    }

    fun selectLanguage(language: AppLanguage) {
        viewModelScope.launch {
            val previousLanguage = selectedLanguage.value
            settingDataStore.setAppLanguage(language)
            val isPatched = userRepository.patchUserLanguage(language.code.uppercase())
            _selectedLanguage.value = language
            if (isPatched && previousLanguage != language) {
                _languageChanged.emit(Unit)
            }
            applyLanguage(language)
        }
    }

    private fun applyLanguage(language: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
