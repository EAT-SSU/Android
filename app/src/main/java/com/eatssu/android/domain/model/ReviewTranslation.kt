package com.eatssu.android.domain.model

data class ReviewTranslation(
    val reviewId: Long,
    val language: String,
    val translatedContent: String,
    val cached: Boolean,
)
