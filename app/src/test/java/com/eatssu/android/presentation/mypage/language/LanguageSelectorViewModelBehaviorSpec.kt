package com.eatssu.android.presentation.mypage.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.repository.UserRepository
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
        val userRepository = mockk<UserRepository>()
        val languageFlow = MutableStateFlow(AppLanguage.KOREAN)
        every { settingDataStore.appLanguage } returns languageFlow
        coEvery { settingDataStore.setAppLanguage(any()) } returns Unit
        coEvery { userRepository.patchUserLanguage(any()) } returns true

        mockkStatic(AppCompatDelegate::class)
        every { AppCompatDelegate.setApplicationLocales(any()) } just runs

        `when`("초기화되면") {
            val viewModel = LanguageSelectorViewModel(settingDataStore, userRepository)

            then("DataStore 언어를 selectedLanguage에 반영한다") {
                runTest {
                    advanceUntilIdle()
                    viewModel.selectedLanguage.value shouldBe AppLanguage.KOREAN
                }
            }
        }

        `when`("언어를 선택하면") {
            val viewModel = LanguageSelectorViewModel(settingDataStore, userRepository)

            then("DataStore 저장과 AppCompat locale 적용을 수행한다") {
                runTest {
                    viewModel.selectLanguage(AppLanguage.ENGLISH)
                    advanceUntilIdle()

                    coVerify { settingDataStore.setAppLanguage(AppLanguage.ENGLISH) }
                    coVerify { userRepository.patchUserLanguage(AppLanguage.ENGLISH.code.uppercase()) }
                    verify {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                AppLanguage.ENGLISH.code
                            )
                        )
                    }
                    viewModel.selectedLanguage.value shouldBe AppLanguage.ENGLISH
                }
            }
        }
    }
})
