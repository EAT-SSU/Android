package com.eatssu.android.presentation.map

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.*
import com.eatssu.android.presentation.map.component.MajorBottomSheet
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.component.MapRestaurantInfo
import com.eatssu.android.presentation.map.component.PlaceType
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.*

private const val DEFAULT_LATITUDE = 37.49517278813046
private const val DEFAULT_LONGITUDE = 126.95661313346206
private const val DEFAULT_ZOOM = 15.5

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapFragmentComposeView(
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(FilterType.All) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
            DEFAULT_ZOOM
        )
    }

    LaunchedEffect(selectedFilter) {
        when (selectedFilter) {
            FilterType.All -> viewModel.loadPartnerships()
            FilterType.Mine -> viewModel.loadUserCollegePartnerships()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "제휴 지도",
                        style = EatssuTheme.typography.subtitle1
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp),
                windowInsets = TopAppBarDefaults.windowInsets
            )
        },
    ) { innerPadding ->

        if (uiState.showDepartmentBottomSheet) {
            MajorBottomSheet(
                onDismiss = { viewModel.toggleDepartmentBottomSheet() },
                onInputClick = {
                    viewModel.toggleDepartmentBottomSheet()
                    val intent = Intent(context, UserInfoActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            NaverMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                uiState.partnerships.forEach { partnership ->
                    val markerState = rememberMarkerState(position = LatLng(partnership.latitude, partnership.longitude))
                    Marker(
                        state = markerState,
                        captionText = partnership.storeName,
                        onClick = {
                            // 마커 클릭 시 제휴 정보 로딩
                            viewModel.selectPartnershipByStoreName(partnership.storeName)
                            true
                        }
                    )
                }
            }

            MapFilterToggle(
                selected = selectedFilter,
                onSelectedChange = { selectedFilter = it },
                modifier = Modifier.padding(top = 12.dp)
            )

            // 찜 기능
//            FloatingActionButton(
//                onClick = { /* TODO */ },
//                containerColor = White,
//                elevation = FloatingActionButtonDefaults.elevation(4.dp),
//                shape = CircleShape,
//                modifier = Modifier
//                    .padding(top = 12.dp, end = 16.dp)
//                    .border(width = 1.dp, color = Gray300, shape = CircleShape)
//                    .size(40.dp)
//                    .align(Alignment.TopEnd)
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_like),
//                    contentDescription = "좋아요",
//                    modifier = Modifier.size(20.dp)
//                )
//            }

            if (uiState.showPartnershipBottomSheet) {
                MapRestaurantBottomSheet(
                    storeName = uiState.restaurantPartnershipInfo!!.storeName,
                    placeType = uiState.restaurantPartnershipInfo!!.restaurantType.let {
                        when (it) {
                            "카페" -> PlaceType.CAFE
                            "음식점" -> PlaceType.RESTAURANT
                            "주점" -> PlaceType.Alcohol
                            else -> PlaceType.RESTAURANT
                        }
                    },
                    mapRestaurantList = uiState.mapRestaurantInfos,
                    onDismiss = { viewModel.togglePartnershipBottomSheet() }
                )
            }

        }
    }
}


enum class FilterType {
    All, Mine
}

@Composable
fun MapFilterToggle(
    selected: FilterType,
    onSelectedChange: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(1.dp, Gray300, shape = CircleShape)
            .clip(CircleShape)
            .background(White)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterItem(
            text = "전체",
            isSelected = selected == FilterType.All,
            onClick = { onSelectedChange(FilterType.All) }
        )
        FilterItem(
            text = "내 제휴",
            isSelected = selected == FilterType.Mine,
            onClick = { onSelectedChange(FilterType.Mine) }
        )
    }
}

@Composable
fun FilterItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Primary else Color.Transparent
    val textColor = if (isSelected) Color.White else Gray600

    Box(
        modifier = Modifier
            .width(65.dp)
            .height(32.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = EatssuTheme.typography.body2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MapFragmentComposeViewPreview() {
    EatssuTheme {
        MapFragmentComposeView()
    }
}
