package com.eatssu.android.presentation.map

import android.content.Context
import com.eatssu.android.domain.model.PartnershipRestaurant
import javax.inject.Inject

class MapExternalNavigator @Inject constructor(
    private val kakaoLocalPlaceResolver: KakaoLocalPlaceResolver,
) {
    suspend fun open(
        context: Context,
        provider: MapProvider,
        restaurant: PartnershipRestaurant,
    ): Boolean {
        val serverWebUrl = MapDeepLink.serverWebUrl(provider, restaurant)
        val serverKakaoPlaceId = restaurant.kakaoMapUrl
            ?.let(MapDeepLink::kakaoPlaceId)
        val isMapAppInstalled = context.isPackageInstalled(provider.packageName)

        if (
            provider == MapProvider.NAVER &&
            !isMapAppInstalled &&
            serverWebUrl != null
        ) {
            return context.openWebUrl(serverWebUrl)
        }

        if (
            provider == MapProvider.NAVER &&
            isMapAppInstalled &&
            context.startServerUrlInMapApp(serverWebUrl, provider)
        ) {
            return true
        }

        val supportsKakaoPlaceAction = MapDeepLink.supportsKakaoPlaceAction(
            context.packageVersionName(MapProvider.KAKAO.packageName),
        )
        val requiresExactPlaceName = provider == MapProvider.NAVER ||
            serverKakaoPlaceId == null ||
            !supportsKakaoPlaceAction
        val resolvedPlace = if (requiresExactPlaceName) {
            kakaoLocalPlaceResolver.resolve(
                restaurant = restaurant,
                preferredPlaceId = serverKakaoPlaceId,
            )
        } else {
            null
        }
        val webFallbackUrl = serverWebUrl ?: MapDeepLink.fallbackWebUrl(
            provider = provider,
            restaurant = restaurant,
            resolvedPlace = resolvedPlace,
        )

        if (!isMapAppInstalled) return context.openWebUrl(webFallbackUrl)

        val appUrl = when (provider) {
            MapProvider.NAVER -> resolvedPlace?.let { place ->
                MapDeepLink.naverSearchUrl(
                    storeName = place.name,
                    appName = context.packageName,
                )
            } ?: MapDeepLink.naverCoordinateUrl(
                storeName = restaurant.storeName,
                latitude = restaurant.latitude,
                longitude = restaurant.longitude,
                appName = context.packageName,
            )

            MapProvider.KAKAO -> {
                val placeId = serverKakaoPlaceId ?: resolvedPlace?.id
                when {
                    supportsKakaoPlaceAction && placeId != null ->
                        MapDeepLink.kakaoPlaceUrl(placeId)

                    resolvedPlace != null -> MapDeepLink.kakaoSearchUrl(
                        storeName = resolvedPlace.name,
                        latitude = restaurant.latitude,
                        longitude = restaurant.longitude,
                    )

                    else -> MapDeepLink.kakaoCoordinateUrl(
                        latitude = restaurant.latitude,
                        longitude = restaurant.longitude,
                    )
                }
            }
        }

        return context.startMapUrl(appUrl, provider) || context.openWebUrl(webFallbackUrl)
    }
}
