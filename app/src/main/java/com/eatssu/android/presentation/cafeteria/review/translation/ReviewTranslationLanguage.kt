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
    content.any { character ->
        character.code in HANGUL_SYLLABLE_RANGE ||
            character.code in HANGUL_JAMO_RANGE ||
            character.code in HANGUL_COMPATIBILITY_JAMO_RANGE
    }

private val HANGUL_SYLLABLE_RANGE = 0xAC00..0xD7A3
private val HANGUL_JAMO_RANGE = 0x1100..0x11FF
private val HANGUL_COMPATIBILITY_JAMO_RANGE = 0x3130..0x318F
private const val ENGLISH_APP_LANGUAGE_CODE = "en"
private const val ENGLISH_TARGET_LANGUAGE = "EN"
