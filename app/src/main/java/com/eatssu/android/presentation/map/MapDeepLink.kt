package com.eatssu.android.presentation.map

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri
import com.eatssu.android.domain.model.PartnershipRestaurant
import timber.log.Timber

enum class MapProvider(
    val packageName: String,
) {
    NAVER("com.nhn.android.nmap"),
    KAKAO("net.daum.android.map"),
}

internal data class ResolvedMapPlace(
    val id: String,
    val name: String,
)

internal object MapDeepLink {
    private const val KAKAO_PLACE_HOST = "place.map.kakao.com"

    fun naverSearchUrl(storeName: String, appName: String): String =
        "nmap://search?query=${storeName.urlEncoded()}&appname=${appName.urlEncoded()}"

    fun naverCoordinateUrl(
        storeName: String,
        latitude: Double,
        longitude: Double,
        appName: String,
    ): String = "nmap://place" +
        "?lat=$latitude" +
        "&lng=$longitude" +
        "&name=${storeName.urlEncoded()}" +
        "&appname=${appName.urlEncoded()}"

    fun kakaoPlaceUrl(placeId: String): String = "kakaomap://place?id=$placeId"

    fun kakaoSearchUrl(
        storeName: String,
        latitude: Double,
        longitude: Double,
    ): String = "kakaomap://search" +
        "?q=${storeName.urlEncoded()}" +
        "&p=$latitude,$longitude"

    fun kakaoCoordinateUrl(latitude: Double, longitude: Double): String =
        "kakaomap://look?p=$latitude,$longitude"

    fun serverWebUrl(provider: MapProvider, restaurant: PartnershipRestaurant): String? =
        when (provider) {
            MapProvider.NAVER -> restaurant.naverMapUrl
            MapProvider.KAKAO -> restaurant.kakaoMapUrl
        }?.takeIf { url -> url.isHttpWebUrl() }

    fun fallbackWebUrl(
        provider: MapProvider,
        restaurant: PartnershipRestaurant,
        resolvedPlace: ResolvedMapPlace?,
    ): String = when (provider) {
        MapProvider.NAVER ->
            "https://map.naver.com/p/search/${(resolvedPlace?.name ?: restaurant.storeName).pathEncoded()}"

        MapProvider.KAKAO -> resolvedPlace?.let { place ->
            "https://map.kakao.com/link/map/${place.id}"
        } ?: "https://map.kakao.com/link/map/" +
            "${restaurant.storeName.pathEncoded()},${restaurant.latitude},${restaurant.longitude}"
    }

    fun supportsKakaoPlaceAction(versionName: String?): Boolean =
        versionName
            ?.substringBefore('.')
            ?.toIntOrNull()
            ?.let { majorVersion -> majorVersion >= 6 }
            ?: false

    internal fun kakaoPlaceId(webUrl: String): String? {
        val uri = runCatching { URI(webUrl) }.getOrNull() ?: return null
        if (uri.scheme !in setOf("http", "https")) return null
        if (!uri.host.equals(KAKAO_PLACE_HOST, ignoreCase = true)) return null

        return uri.path
            ?.split('/')
            ?.firstOrNull { it.isNotBlank() }
            ?.takeIf { segment -> segment.all(Char::isDigit) }
    }

    private fun String.urlEncoded(): String =
        URLEncoder.encode(this, StandardCharsets.UTF_8.name())

    private fun String.pathEncoded(): String = urlEncoded().replace("+", "%20")

    private fun String.isHttpWebUrl(): Boolean = runCatching {
        val uri = URI(this)
        uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank()
    }.getOrDefault(false)
}

internal fun Context.openMapWithoutResolution(
    provider: MapProvider,
    restaurant: PartnershipRestaurant,
): Boolean {
    if (!isPackageInstalled(provider.packageName)) {
        return openWebUrl(
            MapDeepLink.serverWebUrl(provider, restaurant)
                ?: MapDeepLink.fallbackWebUrl(provider, restaurant, resolvedPlace = null),
        )
    }

    val appUrl = when (provider) {
        MapProvider.NAVER -> MapDeepLink.naverCoordinateUrl(
            storeName = restaurant.storeName,
            latitude = restaurant.latitude,
            longitude = restaurant.longitude,
            appName = packageName,
        )

        MapProvider.KAKAO -> MapDeepLink.kakaoCoordinateUrl(
            latitude = restaurant.latitude,
            longitude = restaurant.longitude,
        )
    }

    return startMapUrl(appUrl, provider) ||
        openWebUrl(
            MapDeepLink.serverWebUrl(provider, restaurant)
                ?: MapDeepLink.fallbackWebUrl(provider, restaurant, resolvedPlace = null),
        )
}

internal fun Context.startMapUrl(url: String, provider: MapProvider): Boolean =
    startMapIntent(mapIntent(url, provider.packageName), provider)

internal fun Context.startServerUrlInMapApp(
    url: String?,
    provider: MapProvider,
): Boolean {
    val webUrl = url ?: return false
    val uri = webUrl.toUri()
    if (uri.scheme !in setOf("http", "https")) return false

    val intent = mapIntent(webUrl, provider.packageName)
    if (intent.resolveActivity(packageManager) == null) return false

    return startMapIntent(intent, provider)
}

internal fun Context.openWebUrl(url: String?): Boolean {
    val webUrl = url ?: return false
    val uri = webUrl.toUri()
    if (uri.scheme !in setOf("http", "https")) return false

    return startMapIntent(mapIntent(webUrl), provider = null)
}

internal fun Context.isPackageInstalled(packageName: String): Boolean =
    packageInfo(packageName) != null

internal fun Context.packageVersionName(packageName: String): String? =
    packageInfo(packageName)?.versionName

@Suppress("DEPRECATION")
private fun Context.packageInfo(packageName: String): PackageInfo? =
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }.getOrNull()

private fun Context.mapIntent(url: String, packageName: String? = null): Intent =
    Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        packageName?.let(::setPackage)
        if (this@mapIntent !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

private fun Context.startMapIntent(intent: Intent, provider: MapProvider?): Boolean =
    runCatching {
        startActivity(intent)
    }.onFailure { throwable ->
        Timber.w(throwable, "Failed to open %s map intent", provider?.name ?: "web")
    }.isSuccess
