package com.eatssu.android.presentation.cafeteria.review.translation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.util.Locale

@Composable
fun currentReviewTranslationTargetLanguage(): String {
    val configuration = LocalConfiguration.current
    val locale = ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
    return when (locale.language.lowercase()) {
        "en" -> "EN"
        "ja" -> "JA"
        "vi" -> "VI"
        else -> "KO"
    }
}

fun shouldShowReviewTranslationAction(targetLanguage: String): Boolean = targetLanguage in setOf("KO", "EN", "JA", "VI")
