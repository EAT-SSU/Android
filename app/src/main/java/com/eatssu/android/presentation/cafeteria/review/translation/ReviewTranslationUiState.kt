package com.eatssu.android.presentation.cafeteria.review.translation

data class ReviewTranslationUiState(
    val translatedContent: String? = null,
    val isLoading: Boolean = false,
    val isTranslated: Boolean = false,
    val isUnavailable: Boolean = false,
)
