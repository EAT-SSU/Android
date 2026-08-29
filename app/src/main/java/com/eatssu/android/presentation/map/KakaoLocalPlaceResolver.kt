package com.eatssu.android.presentation.map

import com.eatssu.android.BuildConfig
import com.eatssu.android.data.remote.dto.response.KakaoLocalSearchResponse
import com.eatssu.android.data.remote.service.KakaoLocalService
import timber.log.Timber
import javax.inject.Inject

class KakaoLocalPlaceResolver @Inject constructor(
    private val kakaoLocalService: KakaoLocalService,
) {
    internal suspend fun resolve(
        destination: MapDestination,
        preferredPlaceId: String?,
    ): ResolvedMapPlace? {
        val restApiKey = BuildConfig.KAKAO_REST_API_KEY.trim()
        if (restApiKey.isEmpty()) return null

        val response = runCatching {
            kakaoLocalService.searchPlaces(
                authorization = "KakaoAK $restApiKey",
                query = destination.storeName,
                longitude = destination.longitude,
                latitude = destination.latitude,
            )
        }.onFailure { throwable ->
            Timber.w(throwable, "Failed to resolve Kakao local place")
        }.getOrNull() ?: return null

        if (!response.isSuccessful) {
            Timber.w("Kakao local place search failed: %s", response.code())
            return null
        }

        val matched = selectBestKakaoPlace(
            documents = response.body()?.documents.orEmpty(),
            preferredPlaceId = preferredPlaceId,
        ) ?: return null

        return ResolvedMapPlace(
            id = matched.id,
            name = matched.placeName,
        )
    }
}

internal fun selectBestKakaoPlace(
    documents: List<KakaoLocalSearchResponse.Document>,
    preferredPlaceId: String?,
): KakaoLocalSearchResponse.Document? {
    val matched = documents.firstOrNull { document -> document.id == preferredPlaceId }
        ?: documents.minByOrNull(KakaoLocalSearchResponse.Document::distanceInMeters)
        ?: return null

    return matched.takeIf { document ->
        document.distanceInMeters() <= MAX_MATCH_DISTANCE_METERS
    }
}

private const val MAX_MATCH_DISTANCE_METERS = 300
