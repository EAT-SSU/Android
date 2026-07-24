package com.eatssu.android.domain.model

data class ReviewTranslation(
    val reviewId: Long,
    val sourceLanguage: String,
    val targetLanguage: String,
    val translatedContent: String,
    val provider: String,
    val cached: Boolean,
)
