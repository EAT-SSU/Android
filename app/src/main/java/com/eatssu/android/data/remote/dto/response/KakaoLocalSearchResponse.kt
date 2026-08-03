package com.eatssu.android.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KakaoLocalSearchResponse(
    @SerialName("documents")
    val documents: List<Document> = emptyList(),
) {
    @Serializable
    data class Document(
        @SerialName("id")
        val id: String = "",
        @SerialName("place_name")
        val placeName: String = "",
        @SerialName("distance")
        val distance: String = "",
    ) {
        fun distanceInMeters(): Int = distance.toIntOrNull() ?: Int.MAX_VALUE
    }
}
