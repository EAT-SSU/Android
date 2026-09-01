@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalNaverMapApi::class)

package com.eatssu.android.presentation.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatssu.android.R
import com.eatssu.android.analytics.LocalAnalyticsTracker
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.presentation.MainState
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.goodprice.GoodPriceMapRoute
import com.eatssu.android.presentation.map.component.DepartmentBottomSheet
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.component.PartnershipCategory
import com.eatssu.android.presentation.map.component.PartnershipCategoryFilterRow
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.analytics.MapAnalyticsEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.StoreType
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.White
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.clustering.ClusteringKey
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.Clustering
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

private const val DEFAULT_LATITUDE = 37.49517278813046
private const val DEFAULT_LONGITUDE = 126.95661313346206
private const val DEFAULT_ZOOM = 14.5
private const val PERMISSION_REQUEST_CODE = 1001

/**
 * 숭실대 사용자 지도 화면 (학교 제휴 / 착한 가격 탭 전환 지원)
 */
@Composable
fun MapRoute(
    viewModel: MapViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    mapExternalNavigator: MapExternalNavigator? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // UiState에서 Success 상태인 실제 MapState 데이터만 추출
    val mapState: MapState = when (val s = uiState) {
        is UiState.Success -> s.data
        else -> MapState()
    }

    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val departmentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val partnershipSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val activity = remember(context) { context.findActivityOrNull() }
        ?: throw IllegalStateException("FusedLocationSource는 Activity에서만 사용할 수 있습니다.")
    val scope = rememberCoroutineScope()

    val departmentId by viewModel.departmentId.collectAsStateWithLifecycle()
    val collegeId by viewModel.collegeId.collectAsStateWithLifecycle()

    // 상단 탭 인덱스 (0: 학교 제휴, 1: 착한 가격)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

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
            context.showToast(
                UiText.StringResource(R.string.dialog_location_permission_description),
                ToastType.INFO
            )
        }
    }

    // MainState에서 학과 정보 가져오기
    val showUserDepartmentBottomSheet = when (val state = mainUiState) {
        is UiState.Success -> {
            when (val data = state.data) {
                is MainState.DepartmentState -> data.showUserDepartmentBottomSheet
                else -> false
            }
        }
        else -> false
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> context.showToast(event)
            }
        }
    }

    // 최초 실행 시 위치 권한 요청
    LaunchedEffect(Unit) {
        val fine =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // [학교 제휴 탭] 학과 미입력 시 바텀시트 노출
    LaunchedEffect(showUserDepartmentBottomSheet, selectedTabIndex) {
        if (selectedTabIndex == 0 && showUserDepartmentBottomSheet) {
            departmentSheetState.show()
        } else if (selectedTabIndex == 1) {
            departmentSheetState.hide()
        }
    }

    // [학교 제휴 탭] 필터 변경 결과(RequiresDepartment)에 따라 학과 입력 BottomSheet 표시
    LaunchedEffect(mapState.filterChangeResult, selectedTabIndex) {
        if (selectedTabIndex == 0 && mapState.filterChangeResult is MapState.FilterChangeResult.RequiresDepartment) {
            departmentSheetState.show()
        }
    }

    // 제휴 정보가 선택되면 BottomSheet 표시
    LaunchedEffect(mapState.restaurantPartnershipInfo) {
        if (mapState.restaurantPartnershipInfo != null) {
            partnershipSheetState.show()
        }
        Timber.d("선택된 식당 제휴 정보: ${mapState.restaurantPartnershipInfo}")
    }

    // Screen View 기록
    TrackScreenViewEvent(ScreenId.MAP_MAIN)

    val lifecycleOwner = LocalLifecycleOwner.current

    // onResume 시마다 학과 정보 및 제휴 데이터 갱신
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Timber.d("MapFragmentComposeView: onResume -> 학과 정보 및 제휴 데이터 갱신")
                mainViewModel.refreshUserDepartment()
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 17.dp)
                        .height(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.nav_map),
                        style = EatssuTheme.typography.subtitle1,
                        color = Gray700,
                        textAlign = TextAlign.Center,
                    )
                }

                // 상단 탭: 학교 제휴 | 착한 가격
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = White,
                    contentColor = Primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Primary,
                            height = 2.dp,
                        )
                    },
                    divider = {
                        HorizontalDivider(color = Gray300, thickness = 0.5.dp)
                    },
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                text = stringResource(R.string.tab_school_partnership),
                                style = EatssuTheme.typography.subtitle2.copy(
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                ),
                                color = if (selectedTabIndex == 0) Primary else Gray500,
                            )
                        },
                    )

                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Text(
                                text = stringResource(R.string.tab_good_price),
                                style = EatssuTheme.typography.subtitle2.copy(
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                ),
                                color = if (selectedTabIndex == 1) Primary else Gray500,
                            )
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedTabIndex) {
                // 0: 학교 제휴 탭 (숭실대 중심 제휴 지도)
                0 -> {
                    MapScreen(
                        mapState = mapState,
                        viewModel = viewModel,
                        cameraPositionState = cameraPositionState,
                        locationSource = locationSource,
                        departmentSheetState = departmentSheetState,
                        partnershipSheetState = partnershipSheetState,
                        showToast = { uiText, info ->
                            scope.launch { context.showToast(uiText, info) }
                        },
                        openMap = { provider, restaurant ->
                            scope.launch {
                                val opened = mapExternalNavigator?.open(
                                    context = context,
                                    provider = provider,
                                    restaurant = restaurant,
                                ) ?: context.openMapWithoutResolution(provider, restaurant)

                                if (!opened) {
                                    context.showToast(
                                        UiText.StringResource(R.string.toast_map_open_failed),
                                        ToastType.ERROR,
                                    )
                                }
                            }
                        },
                        navigateToUserInfo = {
                            val intent = Intent(context, UserInfoActivity::class.java)
                            context.startActivity(intent)
                        },
                        onHideDepartmentSheet = {
                            scope.launch {
                                departmentSheetState.hide()
                                viewModel.clearFilterChangeResult()
                            }
                        },
                        onHidePartnershipSheet = {
                            scope.launch {
                                partnershipSheetState.hide()
                                viewModel.clearSelectedPartnership()
                            }
                        },
                        animateCameraPositionTo = { position, currentZoom ->
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdate.toCameraPosition(
                                        CameraPosition(
                                            position,
                                            currentZoom + 2.0
                                        )
                                    )
                                )
                            }
                        },
                        onSelectedCategoryChange = { category ->
                            viewModel.setCategory(category)
                        },
                        departmentId = departmentId,
                        collegeId = collegeId,
                        selectedCategory = mapState.selectedCategory,
                    )
                }

                // 1: 착한 가격 탭 (서울시 전역 착한가격업소 지도, 학과 바텀시트 불필요)
                1 -> {
                    GoodPriceMapRoute(
                        mapExternalNavigator = mapExternalNavigator,
                        showTopBar = false,
                        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.bottom_nav_height)),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
internal fun MapScreen(
    mapState: MapState,
    viewModel: MapViewModel,
    cameraPositionState: CameraPositionState,
    locationSource: FusedLocationSource,
    departmentSheetState: SheetState,
    partnershipSheetState: SheetState,
    showToast: (UiText, ToastType) -> Unit,
    openMap: (MapProvider, PartnershipRestaurant) -> Unit,
    navigateToUserInfo: () -> Unit,
    onHideDepartmentSheet: () -> Unit = {},
    onHidePartnershipSheet: () -> Unit = {},
    animateCameraPositionTo: (LatLng, Double) -> Unit,
    onSelectedCategoryChange: (PartnershipCategory) -> Unit,
    departmentId: Long,
    collegeId: Long,
    selectedCategory: PartnershipCategory,
) {
    val analyticsTracker = LocalAnalyticsTracker.current

    // 학과 정보가 없을 때 보여줄 BottomSheet
    if (departmentSheetState.isVisible) {
        Timber.d("학과 정보가 없습니다. BottomSheet를 표시합니다.")

        DepartmentBottomSheet(
            onDismiss = {
                onHideDepartmentSheet()
            },
            onInputClick = {
                onHideDepartmentSheet()
                navigateToUserInfo()
            },
            sheetState = departmentSheetState,
        )
    }

    // 특정 식당에 대한 제휴 정보 BottomSheet
    if (partnershipSheetState.isVisible) {
        mapState.restaurantPartnershipInfo?.let { info ->
            mapState.storeType?.let { storeType ->
                LaunchedEffect(info.id, collegeId, departmentId) {
                    analyticsTracker.track(
                        MapAnalyticsEvent.PartnerRestaurantClicked(
                            college = collegeId,
                            major = departmentId,
                            partnerRestaurantId = info.id.toLong(),
                        ),
                    )
                }

                MapRestaurantBottomSheet(
                    storeName = info.storeName,
                    storeType = storeType,
                    mapRestaurantList = mapState.restaurantInfoList,
                    onNaverMapClick = {
                        openMap(MapProvider.NAVER, info)
                    },
                    onKakaoMapClick = {
                        openMap(MapProvider.KAKAO, info)
                    },
                    onDismiss = {
                        onHidePartnershipSheet()
                    },
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                isZoomControlEnabled = false,
                isLocationButtonEnabled = true,
            ),
            locationSource = locationSource,
            contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.bottom_nav_height)),
            properties = MapProperties(
                // 현재 다른 위치에 있는 경우에도 숭실대입구를 보여주어야 함
                locationTrackingMode = LocationTrackingMode.NoFollow,
            ),
        ) {
            val clusterItems = mapState.visiblePartnerships.associateBy {
                ItemKey(
                    it.storeName,
                    LatLng(it.latitude, it.longitude),
                )
            }

            Clustering(
                items = clusterItems,
                thresholdStrategy = { 25.0 },
                clusterContent = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${it.size}",
                            color = Color.White,
                            style = EatssuTheme.typography.body2,
                        )
                    }
                },
                leafContent = { info ->
                    val partnership = info.tag as? Partnership
                    if (partnership != null) {
                        Row(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(13.dp))
                                .border(1.dp, Gray300, RoundedCornerShape(13.dp))
                                .padding(
                                    start = 3.dp,
                                    end = 7.dp,
                                    top = 2.5.dp,
                                    bottom = 2.5.dp,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = spacedBy(3.dp),
                        ) {
                            val iconRes = when (partnership.restaurantType) {
                                StoreType.CAFE -> R.drawable.ic_map_marker_cafe
                                StoreType.PUB -> R.drawable.ic_map_marker_pub
                                else -> R.drawable.ic_map_marker_restaurant
                            }

                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )

                            Text(
                                text = partnership.storeName,
                                style = EatssuTheme.typography.caption3,
                                color = Color.Black,
                            )
                        }
                    }
                },
                onClickCluster = { info, _ ->
                    animateCameraPositionTo(info.position, cameraPositionState.position.zoom)
                    true
                },
                onClickLeaf = { info, _ ->
                    val partnership = info.tag as? Partnership ?: return@Clustering true

                    if (partnership.partnershipInfos.isEmpty()) {
                        // 제휴 정보가 없을 때는 토스트만 띄우고 바텀시트는 안 띄움
                        showToast(
                            UiText.StringResource(R.string.toast_partnership_info_not_found),
                            ToastType.INFO,
                        )
                    } else {
                        // 제휴 정보가 있을 때만 바텀시트 띄움
                        viewModel.selectPartnershipByStoreName(partnership.storeName)
                    }
                    true
                },
            )
        }

        PartnershipCategoryFilterRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { next ->
                if (partnershipSheetState.isVisible) return@PartnershipCategoryFilterRow
                onSelectedCategoryChange(next)
            },
            modifier = Modifier.padding(top = 12.dp),
        )
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
        MapRoute()
    }
}

data class ItemKey(val id: String, private val latLng: LatLng) : ClusteringKey {
    override fun getPosition() = latLng
}
