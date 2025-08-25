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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.compose.ui.theme.*
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.map.component.DepartmentBottomSheet
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.component.PartnershipFilterToggle
import com.eatssu.android.presentation.map.model.PlaceType
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import timber.log.Timber

private const val DEFAULT_LATITUDE = 37.49517278813046
private const val DEFAULT_LONGITUDE = 126.95661313346206
private const val DEFAULT_ZOOM = 15.5
private const val PERMISSION_REQUEST_CODE = 1000

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapFragmentComposeView(
    viewModel: MapViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val activity = remember(context) { context.findActivityOrNull() }
        ?: throw IllegalStateException("FusedLocationSource는 Activity에서만 사용할 수 있습니다.")
    val scope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf(FilterType.All) }

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
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
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
    LaunchedEffect(mainUiState.showUserDepartmentBottomSheet) {
        if (mainUiState.showUserDepartmentBottomSheet) {
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
                    mapRestaurantList = uiState.restaurantInfos,
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
                properties = MapProperties(
                    locationTrackingMode = LocationTrackingMode.Follow,
                ),
                onLocationChange = { location ->
                    // 위치가 업데이트되면 위치 권한 있다고 간주
                    hasLocationPermission = true
                },
                onMapClick = { _, _ ->
                    // 만약 지도 클릭이 발생했는데 권한이 없고 위치가 null이라면 권한 요청
                    if (!hasLocationPermission) {
                        Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
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
                    if (uiState.showPartnershipBottomSheet) return@PartnershipFilterToggle

                    val hasDepartment = mainUiState.departmentName.isNotBlank()

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
