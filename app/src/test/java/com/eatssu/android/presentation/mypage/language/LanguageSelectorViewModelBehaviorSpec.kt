package com.eatssu.android.presentation.mypage.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.AppLanguage
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageSelectorViewModelBehaviorSpec : AppBehaviorSpec({

    given("언어 선택") {
        val settingDataStore = mockk<SettingDataStore>()
        val languageFlow = MutableStateFlow(AppLanguage.KOREAN)
        every { settingDataStore.appLanguage } returns languageFlow
        coEvery { settingDataStore.setAppLanguage(any()) } returns Unit

        mockkStatic(AppCompatDelegate::class)
        every { AppCompatDelegate.setApplicationLocales(any()) } just runs

        `when`("초기화되면") {
            val viewModel = LanguageSelectorViewModel(settingDataStore)

            then("DataStore 언어를 selectedLanguage에 반영한다") {
                runTest {
                    advanceUntilIdle()
                    viewModel.selectedLanguage.value shouldBe AppLanguage.KOREAN
                }
            }
        }

        `when`("언어를 선택하면") {
            val viewModel = LanguageSelectorViewModel(settingDataStore)

            then("DataStore 저장과 AppCompat locale 적용을 수행한다") {
                runTest {
                    viewModel.selectLanguage(AppLanguage.KOREAN)
                    advanceUntilIdle()

                    coVerify { settingDataStore.setAppLanguage(AppLanguage.KOREAN) }
                    verify { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(AppLanguage.KOREAN.code)) }
                    viewModel.selectedLanguage.value shouldBe AppLanguage.KOREAN
                }
            }
        }
    }
})
