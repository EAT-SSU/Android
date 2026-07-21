package com.eatssu.android.presentation.cafeteria.review.translation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun currentReviewTranslationTargetLanguage(): String? =
    reviewTranslationTargetLanguage(LocalConfiguration.current.locales[0].language)

fun reviewTranslationTargetLanguage(appLanguageCode: String): String? =
    ENGLISH_TARGET_LANGUAGE.takeIf { appLanguageCode == ENGLISH_APP_LANGUAGE_CODE }

fun shouldShowReviewTranslationAction(
    targetLanguage: String?,
    isLoggedIn: Boolean,
    content: String,
): Boolean = isLoggedIn &&
    targetLanguage == ENGLISH_TARGET_LANGUAGE &&
    content.any { it.isLetter() && it.code > ASCII_MAX_CODE }

private const val ASCII_MAX_CODE = 0x7F
private const val ENGLISH_APP_LANGUAGE_CODE = "en"
private const val ENGLISH_TARGET_LANGUAGE = "EN"
