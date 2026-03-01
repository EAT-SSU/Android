@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalNaverMapApi::class)

package com.eatssu.android.presentation.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.presentation.map.component.DepartmentBottomSheet
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.map.component.MapRestaurantBottomSheet
import com.eatssu.android.presentation.map.component.PartnershipFilterToggle
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.EventLogger
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.StoreType
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
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
import kotlinx.coroutines.launch
import timber.log.Timber

private const val DEFAULT_LATITUDE = 37.49517278813046
private const val DEFAULT_LONGITUDE = 126.95661313346206
private const val DEFAULT_ZOOM = 17.5
private const val PERMISSION_REQUEST_CODE = 1001

@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToUserInfo: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // MapUiState에서 MapState 데이터만 추출
    val mapState: MapState = (uiState as? MapState) ?: MapState()

    val departmentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val partnershipSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val activity = remember(context) { context.findActivityOrNull() }
        ?: throw IllegalStateException("FusedLocationSource는 Activity에서만 사용할 수 있습니다.")
    val scope = rememberCoroutineScope()

    val departmentId by viewModel.departmentId.collectAsStateWithLifecycle()
    val collegeId by viewModel.collegeId.collectAsStateWithLifecycle()

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

    val departmentName = mapState.departmentName
    val showUserDepartmentBottomSheet = mapState.showUserDepartmentBottomSheet

    ObserveUiEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> context.showToast(event)
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

    // 상태 변화 감지해서 show/hide -> Scrim 잔존 문제 해결
    LaunchedEffect(showUserDepartmentBottomSheet) {
        if (showUserDepartmentBottomSheet) {
            departmentSheetState.show()
        } else {
            departmentSheetState.hide()
        }
    }

    // 필터 변경 결과에 따라 학과 입력 BottomSheet 표시
    LaunchedEffect(mapState.filterChangeResult) {
        if (mapState.filterChangeResult is MapState.FilterChangeResult.RequiresDepartment) {
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

    // onResume 시마다 학과 정보 반영
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Timber.d("MapFragmentComposeView: onResume -> 학과 정보 갱신")
                viewModel.refreshUserDepartment()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    MapScreen(
        mapState = mapState,
        viewModel = viewModel,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        departmentSheetState = departmentSheetState,
        partnershipSheetState = partnershipSheetState,
        showToast = { uiText, info ->
            scope.launch {
                context.showToast(uiText, info)
            }
        },
        navigateToUserInfo = navigateToUserInfo,
        onHideDepartmentSheet = {
            scope.launch { departmentSheetState.hide() }
        },
        onHidePartnershipSheet = {
            scope.launch { partnershipSheetState.hide() }
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
        onSelectedFilterChange = { filter ->
            viewModel.setFilter(filter)
        },
        departmentId = departmentId,
        collegeId = collegeId,
        departmentName = departmentName,
        selectedFilter = mapState.selectedFilter,
    )
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
    navigateToUserInfo: () -> Unit,
    onHideDepartmentSheet: () -> Unit = {},
    onHidePartnershipSheet: () -> Unit = {},
    animateCameraPositionTo: (LatLng, Double) -> Unit,
    onSelectedFilterChange: (FilterType) -> Unit,
    departmentId: Long,
    collegeId: Long,
    departmentName: String,
    selectedFilter: FilterType,
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(17.dp))
                Text(
                    text = stringResource(R.string.title_partnership_map),
                    style = EatssuTheme.typography.subtitle1,
                    color = Gray700,
                )
                Spacer(Modifier.height(16.dp))
            }
        },
    ) { innerPadding ->

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
                sheetState = departmentSheetState
            )
        }

        // 특정 식당에 대한 제휴 정보 BottomSheet
        if (partnershipSheetState.isVisible) {
            mapState.restaurantPartnershipInfo?.let { info ->

                mapState.storeType?.let { storeType ->

                    EventLogger.clickPartnerRestaurant(
                        college = collegeId,
                        major = departmentId,
                        partnerRestaurantId = info.id.toLong()
                    )

                    MapRestaurantBottomSheet(
                        storeName = info.storeName,
                        storeType = storeType,
                        mapRestaurantList = mapState.restaurantInfoList,
                        onDismiss = {
                            onHidePartnershipSheet()
                        }
                    )
                }
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
                uiSettings = MapUiSettings(
                    isZoomControlEnabled = false,
                    isLocationButtonEnabled = true
                ),
                locationSource = locationSource,
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.bottom_nav_height)),
                properties = MapProperties(
                    // 현재 다른 위치에 있는 경우에도 숭실대입구를 보여주어야 함
                    locationTrackingMode = LocationTrackingMode.NoFollow,
                ),
            ) {
                val clusterItems = mapState.partnerships.associateBy {
                    ItemKey(
                        it.storeName,
                        LatLng(it.latitude, it.longitude)
                    )
                }

                Clustering(
                    items = clusterItems,
                    thresholdStrategy = {
                        // 줌 레벨에 상관 없이 임의의 값 사용
                        25.0
                    },

                    clusterContent = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${it.size}",
                                color = Color.White,
                                style = EatssuTheme.typography.body2
                            )
                        }
                    },
                    leafContent = { info ->
                        val partnership = info.tag as? Partnership ?: return@Clustering

                        Row(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(13.dp))
                                .border(1.dp, Gray300, RoundedCornerShape(13.dp))
                                .padding(
                                    start = 3.dp,
                                    end = 7.dp,
                                    top = 2.5.dp,
                                    bottom = 2.5.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = spacedBy(
                                3.dp
                            )
                        ) {
                            val iconRes = when (partnership.restaurantType) {
                                StoreType.CAFE -> R.drawable.ic_map_marker_cafe
                                StoreType.PUB -> R.drawable.ic_map_marker_pub
                                else -> R.drawable.ic_map_marker_restaurant
                            }

                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = partnership.storeName,
                                style = EatssuTheme.typography.caption3,
                                color = Color.Black
                            )
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
                                ToastType.INFO
                            )
                        } else {
                            // 제휴 정보가 있을 때만 바텀시트 띄움
                            viewModel.selectPartnershipByStoreName(partnership.storeName)
                        }
                        true
                    }

                )
            }

            // 학과 정보를 입력하지 않은 상태에서 제휴 필터를 변경하려고 할 때 BottomSheet 표시
            // 학과 정보가 없으면 제휴 필터를 변경할 수 없음
            PartnershipFilterToggle(
                selected = selectedFilter,
                onSelectedChange = { next ->
                    if (partnershipSheetState.isVisible) return@PartnershipFilterToggle
                    onSelectedFilterChange(next)
                },
                modifier = Modifier.padding(top = 12.dp),
                departmentName = departmentName,
            )
        }
    }
}

// FusedLocationSource는 Activity에서만 활용 가능하기 때문에 확장 함수 생성
fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}

@ThemePreviews
@Composable
fun MapFragmentComposeViewPreview() {
    EatssuTheme {
        MapRoute()
    }
}

data class ItemKey(val id: String, private val latLng: LatLng) : ClusteringKey {
    override fun getPosition() = latLng
}
