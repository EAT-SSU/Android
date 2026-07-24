package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.ReviewTranslation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewTranslationResponse(
    @SerialName("reviewId") val reviewId: Long? = null,
    @SerialName("sourceLanguage") val sourceLanguage: String? = null,
    @SerialName("targetLanguage") val targetLanguage: String? = null,
    @SerialName("translatedContent") val translatedContent: String? = null,
    @SerialName("provider") val provider: String? = null,
    @SerialName("cached") val cached: Boolean? = null,
)

fun ReviewTranslationResponse.toDomain() = ReviewTranslation(
    reviewId = reviewId ?: -1L,
    sourceLanguage = sourceLanguage.orEmpty(),
    targetLanguage = targetLanguage.orEmpty(),
    translatedContent = translatedContent.orEmpty(),
    provider = provider.orEmpty(),
    cached = cached ?: false,
)
