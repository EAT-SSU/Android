package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.ReviewTranslation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewTranslationResponse(
    @SerialName("reviewId") val reviewId: Long? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("translatedContent") val translatedContent: String? = null,
    @SerialName("cached") val cached: Boolean? = null,
)

fun ReviewTranslationResponse.toDomain() = ReviewTranslation(
    reviewId = reviewId ?: -1L,
    language = language.orEmpty(),
    translatedContent = translatedContent.orEmpty(),
    cached = cached ?: false,
)
