package com.eatssu.android.presentation.favorite

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.presentation.map.MapExternalNavigator
import com.eatssu.android.presentation.map.MapProvider
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.model.RestaurantInfo
import com.eatssu.android.presentation.map.openMapWithoutResolution
import com.eatssu.common.UiState
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.theme.White
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun FavoriteDetailRoute(
    partnership: PartnershipRestaurant,
    viewModel: FavoriteDetailViewModel,
    mapExternalNavigator: MapExternalNavigator,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(partnership.id) {
        viewModel.showPartnership(partnership)
    }

    when (val state = uiState) {
        UiState.Init, UiState.Loading -> Box(Modifier.fillMaxSize()) {
            DelayedLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }

        UiState.Error -> LaunchedEffect(Unit) { onBack() }
        is UiState.Success -> FavoriteDetailScreen(
            restaurant = state.data,
            onBack = onBack,
            onNaverMapClick = {
                scope.launch {
                    openMap(context, mapExternalNavigator, MapProvider.NAVER, state.data)
                }
            },
            onKakaoMapClick = {
                scope.launch {
                    openMap(context, mapExternalNavigator, MapProvider.KAKAO, state.data)
                }
            },
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteDetailScreen(
    restaurant: PartnershipRestaurant,
    onBack: () -> Unit,
    onNaverMapClick: () -> Unit,
    onKakaoMapClick: () -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(restaurant.latitude, restaurant.longitude),
            16.5,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                isZoomControlEnabled = false,
                isLocationButtonEnabled = false,
            ),
            properties = MapProperties(),
            contentPadding = PaddingValues(
                bottom = dimensionResource(R.dimen.bottom_nav_height),
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BottomSheetDefaults.ScrimColor),
        )

        Box(
            modifier = Modifier
                .padding(start = 18.dp, top = 30.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(White)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.nav_back),
                modifier = Modifier.size(24.dp),
            )
        }

        MapRestaurantBottomSheet(
            storeName = restaurant.storeName,
            storeType = restaurant.storeType,
            mapRestaurantList = listOf(
                RestaurantInfo(
                    collegeName = restaurant.collegeName,
                    departmentName = restaurant.departmentName,
                    period = "${restaurant.startDate} ~ ${restaurant.endDate}",
                    benefit = restaurant.description,
                ),
            ),
            onNaverMapClick = onNaverMapClick,
            onKakaoMapClick = onKakaoMapClick,
            onDismiss = onBack,
            scrimColor = Color.Transparent,
        )
    }
}

private suspend fun openMap(
    context: Context,
    navigator: MapExternalNavigator,
    provider: MapProvider,
    restaurant: PartnershipRestaurant,
) {
    navigator.open(context, provider, restaurant) ||
            context.openMapWithoutResolution(provider, restaurant)
}
