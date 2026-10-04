package com.eatssu.android.presentation.map

import com.eatssu.android.domain.model.PartnershipRestaurant

internal data class MapDestination(
    val storeName: String,
    val latitude: Double,
    val longitude: Double,
    val naverMapUrl: String? = null,
    val kakaoMapUrl: String? = null,
)

internal fun PartnershipRestaurant.toMapDestination(): MapDestination = MapDestination(
    storeName = storeName,
    latitude = latitude,
    longitude = longitude,
    naverMapUrl = naverMapUrl,
    kakaoMapUrl = kakaoMapUrl,
)
