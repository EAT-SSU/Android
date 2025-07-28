package com.eatssu.android.presentation.map

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.compose.ui.theme.*
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.map.component.MajorBottomSheet
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.component.PartnershipFilterToggle
import com.eatssu.android.presentation.map.component.PlaceType
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.*
import com.naver.maps.map.overlay.OverlayImage
import timber.log.Timber

private const val DEFAULT_LATITUDE = 37.49517278813046
private const val DEFAULT_LONGITUDE = 126.95661313346206
private const val DEFAULT_ZOOM = 15.5

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapFragmentComposeView(
    viewModel: MapViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(FilterType.All) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
            DEFAULT_ZOOM
        )
    }

    // 제휴 정보 토글 event
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
        Timber.d("학과 정보 : ${MySharedPreferences.getUserDepartment(context)}")

        // 학과 정보가 없을 때 보여줄 BottomSheet
        if (mainUiState.showUserDepartmentBottomSheet) {
            Timber.d("학과 정보가 없습니다. BottomSheet를 표시합니다.")

            MajorBottomSheet(
                onDismiss = { viewModel.toggleDepartmentBottomSheet() },
                onInputClick = {
                    viewModel.toggleDepartmentBottomSheet()
                    val intent = Intent(context, UserInfoActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        // 특정 식당에 대한 제휴 정보 BottomSheet
        if (uiState.showPartnershipBottomSheet) {
            uiState.restaurantPartnershipInfo?.let { info ->
                MapRestaurantBottomSheet(
                    storeName = info.storeName,
                    placeType = when (info.restaurantType) {
                        stringResource(R.string.cafe) -> PlaceType.CAFE
                        stringResource(R.string.restaurant) -> PlaceType.RESTAURANT
                        stringResource(R.string.alcohol) -> PlaceType.Alcohol
                        else -> PlaceType.RESTAURANT
                    },
                    mapRestaurantList = uiState.mapRestaurantInfos,
                    onDismiss = { viewModel.togglePartnershipBottomSheet() }
                )
            }
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

                    // TODO 마커 커스텀 방식 수정
                    Marker(
                        icon = OverlayImage.fromResource(
                            when (partnership.restaurantType) {
                                stringResource(R.string.cafe) -> R.drawable.ic_map_marker_cafe
                                stringResource(R.string.restaurant) -> R.drawable.ic_map_marker_restaurant
                                stringResource(R.string.alcohol) -> R.drawable.ic_map_marker_alcohol
                                else -> R.drawable.ic_map_marker_restaurant
                            }
                        ),
                        width = 20.dp,
                        height = 20.dp,
                        captionAligns = arrayOf(Align.Right),
                        state = markerState,
                        captionText = partnership.storeName,
                        captionColor = Black,
                        captionTextSize = 10.sp,
                        isHideCollidedCaptions = true,
                        onClick = {
                            // 마커 클릭 시 제휴 정보 업데이트
                            viewModel.selectPartnershipByStoreName(partnership.storeName)
                            true
                        }
                    )

                }
            }

            PartnershipFilterToggle(
                selected = selectedFilter,
                onSelectedChange = { selectedFilter = it },
                modifier = Modifier.padding(top = 12.dp),
                departmentName = mainUiState.departmentName
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapFragmentComposeViewPreview() {
    EatssuTheme {
        MapFragmentComposeView()
    }
}
