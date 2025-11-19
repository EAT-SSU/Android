package com.eatssu.android.presentation.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatssu.android.R
import com.eatssu.android.domain.model.RestaurantType
import com.eatssu.android.presentation.MainState
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.map.component.DepartmentBottomSheet
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.component.PartnershipFilterToggle
import com.eatssu.android.presentation.map.model.PlaceType
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.EatssuTheme
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.Align
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

private const val DEFAULT_LATITUDE = 37.49517278813046
private const val DEFAULT_LONGITUDE = 126.95661313346206
private const val DEFAULT_ZOOM = 17.5
private const val PERMISSION_REQUEST_CODE = 1001

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapFragmentComposeView(
    viewModel: MapViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // UiState에서 Success 상태인 실제 MapState 데이터만 추출
    val mapState: MapState = when (val s = uiState) {
        is UiState.Success -> s.data
        else -> MapState()
    }

    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val activity = remember(context) { context.findActivityOrNull() }
        ?: throw IllegalStateException("FusedLocationSource는 Activity에서만 사용할 수 있습니다.")
    val scope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf(FilterType.Mine) }

    val departmentId = viewModel.departmentId
    val collegeId = viewModel.collegeId

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
            DEFAULT_ZOOM
        )
    }

    // 위치 추적을 위한 locationSource 생성
    val locationSource = remember {
        FusedLocationSource(activity, PERMISSION_REQUEST_CODE)
    }

    // 위치 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (!granted) {
            Toast.makeText(context, "내 위치를 바로 확인하며 제휴 식당을 찾아볼 수 있도록 위치 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // MainState에서 학과 정보 가져오기
    val (departmentName, showUserDepartmentBottomSheet) = when (val state = mainUiState) {
        is UiState.Success -> {
            when (val data = state.data) {
                is MainState.DepartmentState -> data.departmentName to data.showUserDepartmentBottomSheet
                else -> "학과" to false
            }
        }

        else -> "학과" to false
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 최초 실행 시 위치 권한 요청
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // 상태 변화 감지해서 show/hide -> Scrim 잔존 문제 해결
    LaunchedEffect(showUserDepartmentBottomSheet) {
        if (showUserDepartmentBottomSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 제휴 정보 토글 event
    LaunchedEffect(selectedFilter) {
        when (selectedFilter) {
            FilterType.All -> {
                viewModel.loadPartnerships()
                EventLogger.clickMap()
            }
            FilterType.Mine -> {
                viewModel.loadUserCollegePartnerships()
                EventLogger.clickMapMine(collegeId, departmentId)
            }
        }
    }

    // Screen View 기록
    TrackScreenViewEvent(ScreenId.MAP_MAIN)

    val lifecycleOwner = LocalLifecycleOwner.current

    // onResume 시마다 학과 정보 반영
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Timber.d("MapFragmentComposeView: onResume -> 학과 정보 갱신")
                mainViewModel.refreshUserDepartment()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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

        // 학과 정보가 없을 때 보여줄 BottomSheet
        if (sheetState.isVisible) {
            Timber.d("학과 정보가 없습니다. BottomSheet를 표시합니다.")

            DepartmentBottomSheet(
                onDismiss = { viewModel.toggleDepartmentBottomSheet() },
                onInputClick = {
                    viewModel.toggleDepartmentBottomSheet()
                    val intent = Intent(context, UserInfoActivity::class.java)
                    context.startActivity(intent)
                },
                sheetState = sheetState
            )
        }

        // 특정 식당에 대한 제휴 정보 BottomSheet
        if (mapState.showPartnershipBottomSheet) {
            mapState.restaurantPartnershipInfo?.let { info ->
                EventLogger.clickPartnerRestaurant(
                    college = collegeId,
                    major = departmentId,
                    partnerRestaurantId = info.id.toLong()
                )

                MapRestaurantBottomSheet(
                    storeName = info.storeName,
                    placeType = when (info.restaurantType) {
                        RestaurantType.CAFE -> PlaceType.CAFE
                        RestaurantType.RESTAURANT -> PlaceType.RESTAURANT
                        RestaurantType.PUB -> PlaceType.PUB
                        else -> PlaceType.RESTAURANT
                    },
                    mapRestaurantList = mapState.restaurantInfoList,
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
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(isZoomControlEnabled = false, isLocationButtonEnabled = true),
                locationSource = locationSource,
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.bottom_nav_height)),
                properties = MapProperties(
                    locationTrackingMode = LocationTrackingMode.Follow,
                ),
                onLocationChange = { location ->
                    // 위치가 업데이트되면 위치 권한 있다고 간주
                    hasLocationPermission = true
                },
            ) {
                mapState.partnerships.forEach { partnership ->
                    val markerState = rememberMarkerState(position = LatLng(partnership.latitude, partnership.longitude))

                    Marker(
                        icon = OverlayImage.fromResource(
                            when (partnership.restaurantType) {
                                RestaurantType.CAFE -> R.drawable.ic_map_marker_cafe
                                RestaurantType.RESTAURANT -> R.drawable.ic_map_marker_restaurant
                                RestaurantType.PUB -> R.drawable.ic_map_marker_pub
                            }
                        ),
                        width = 20.dp,
                        height = 20.dp,
                        captionAligns = arrayOf(Align.Bottom),
                        state = markerState,
                        captionText = partnership.storeName,
                        captionColor = Black,
                        captionTextSize = 10.sp,
                        onClick = {
                            if (partnership.partnershipInfos.isEmpty()) {
                                // 제휴 정보가 없을 때는 토스트만 띄우고 바텀시트는 안 띄움
                                Toast.makeText(context, "제휴 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                                true
                            } else {
                                // 제휴 정보가 있을 때만 바텀시트 띄움
                                viewModel.selectPartnershipByStoreName(partnership.storeName)
                                true
                            }
                        }
                    )

                }
            }

            // 학과 정보를 입력하지 않은 상태에서 제휴 필터를 변경하려고 할 때 BottomSheet 표시
            // 학과 정보가 없으면 제휴 필터를 변경할 수 없음
            PartnershipFilterToggle(
                selected = selectedFilter,
                onSelectedChange = { next ->
                    if (mapState.showPartnershipBottomSheet) return@PartnershipFilterToggle

                    val hasDepartment = !departmentName.equals("학과")

                    if (next == FilterType.Mine && !hasDepartment) {
                        // 전환 막기: selectedFilter는 그대로 (All 유지)
                        // 학과 입력 바텀시트 띄우기
                        scope.launch {
                            // suspend 함수이므로 코루틴 내에서 실행
                            sheetState.show()
                        }
                        return@PartnershipFilterToggle
                    }

                    // 학과 정보가 있거나 All 선택은 정상 전환
                    selectedFilter = next
                },
                modifier = Modifier.padding(top = 12.dp),
                departmentName = departmentName.toString()
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

// FusedLocationSource는 Activity에서만 활용 가능하기 때문에 확장 함수 생성
fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun MapFragmentComposeViewPreview() {
    EatssuTheme {
        MapFragmentComposeView()
    }
}
