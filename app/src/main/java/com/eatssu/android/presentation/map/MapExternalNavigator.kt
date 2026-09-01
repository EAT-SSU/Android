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
    ): Boolean = open(context, provider, restaurant.toMapDestination())

    internal suspend fun open(
        context: Context,
        provider: MapProvider,
        destination: MapDestination,
    ): Boolean {
        val serverWebUrl = MapDeepLink.serverWebUrl(provider, destination)
        val serverKakaoPlaceId = destination.kakaoMapUrl
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
                destination = destination,
                preferredPlaceId = serverKakaoPlaceId,
            )
        } else {
            null
        }
        val webFallbackUrl = serverWebUrl ?: MapDeepLink.fallbackWebUrl(
            provider = provider,
            destination = destination,
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
                storeName = destination.storeName,
                latitude = destination.latitude,
                longitude = destination.longitude,
                appName = context.packageName,
            )

            MapProvider.KAKAO -> {
                val placeId = serverKakaoPlaceId ?: resolvedPlace?.id
                when {
                    supportsKakaoPlaceAction && placeId != null ->
                        MapDeepLink.kakaoPlaceUrl(placeId)

                    resolvedPlace != null -> MapDeepLink.kakaoSearchUrl(
                        storeName = resolvedPlace.name,
                        latitude = destination.latitude,
                        longitude = destination.longitude,
                    )

                    else -> MapDeepLink.kakaoCoordinateUrl(
                        latitude = destination.latitude,
                        longitude = destination.longitude,
                    )
                }
            }
        }

        return context.startMapUrl(appUrl, provider) || context.openWebUrl(webFallbackUrl)
    }
}
