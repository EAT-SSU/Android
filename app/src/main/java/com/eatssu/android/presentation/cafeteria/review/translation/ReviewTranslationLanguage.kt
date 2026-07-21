package com.eatssu.android.presentation.cafeteria.review.translation

import androidx.compose.runtime.Composable

@Composable
fun currentReviewTranslationTargetLanguage(): String = "EN"

fun shouldShowReviewTranslationAction(targetLanguage: String): Boolean = targetLanguage == "EN"
