@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalNaverMapApi::class)

package com.eatssu.android.presentation.goodprice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatssu.android.R
import com.eatssu.android.domain.model.GoodPriceStore
import com.eatssu.android.presentation.goodprice.component.GoodPriceFilterRow
import com.eatssu.android.presentation.goodprice.component.GoodPriceStoreBottomSheet
import com.eatssu.android.presentation.map.findActivityOrNull
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.common.UiEvent
import com.eatssu.common.enums.GoodPriceCategory
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.EatSsuSnackbar
import com.eatssu.design_system.component.EatSsuSnackbarType
import com.eatssu.design_system.theme.Black
import com.eatssu.design_system.theme.EatssuTheme
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// 서울 중심 좌표 기본값 (서울시 25개 자치구 착한가격업소 전체 조망용)
private const val SEOUL_CENTER_LATITUDE = 37.5518
private const val SEOUL_CENTER_LONGITUDE = 126.9882
private const val DEFAULT_ZOOM = 11.5
private const val LOCATION_PERMISSION_REQUEST_CODE = 2001

/**
 * 착한가격업소 지도 화면 엔트리포인트 (재사용 가능한 Route 컴포저블)
 */
@Composable
fun GoodPriceMapRoute(
    viewModel: GoodPriceMapViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivityOrNull() }
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 카메라 위치 상태 설정
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(SEOUL_CENTER_LATITUDE, SEOUL_CENTER_LONGITUDE),
            DEFAULT_ZOOM
        )
    }

    // 위치 추적 source 생성
    val locationSource = remember(activity) {
        activity?.let { FusedLocationSource(it, LOCATION_PERMISSION_REQUEST_CODE) }
    }

    // 에러 이벤트 수신 시 디자인 스낵바 메시지 노출
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    errorMessage = event.message.asString(context)
                    delay(3000)
                    errorMessage = null
                }
            }
        }
    }

    // 화면 진입 로깅
    TrackScreenViewEvent(ScreenId.GOOD_PRICE_MAP)

    GoodPriceMapScreen(
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        errorMessage = errorMessage,
        onCategorySelected = { viewModel.setCategory(it) },
        onStoreClick = { viewModel.selectStore(it.id) },
        onDismissBottomSheet = { viewModel.clearSelectedStore() },
        animateCameraTo = { latLng, zoom ->
            scope.launch {
                cameraPositionState.animate(
                    CameraUpdate.toCameraPosition(CameraPosition(latLng, zoom + 2.0))
                )
            }
        },
        modifier = modifier,
    )
}

/**
 * 착한가격업소 지도 UI 컴포넌트
 */
@Composable
fun GoodPriceMapScreen(
    uiState: GoodPriceMapState,
    cameraPositionState: CameraPositionState,
    locationSource: FusedLocationSource?,
    errorMessage: String?,
    onCategorySelected: (GoodPriceCategory) -> Unit,
    onStoreClick: (GoodPriceStore) -> Unit,
    onDismissBottomSheet: () -> Unit,
    animateCameraTo: (LatLng, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    // [성능 최적화 1] 1,500개 아이템 매핑 객체 재생성 및 GC 방지를 위한 remember 캐싱
    val clusterItems = remember(uiState.stores) {
        uiState.stores.associateBy {
            GoodPriceClusterKey(
                id = it.id,
                latLng = LatLng(it.latitude, it.longitude),
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            // 상단 타이틀: 디자인 가이드에 따라 뒤로가기 버튼 없이 가운데 타이틀만 표시
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_good_price_map),
                        style = EatssuTheme.typography.subtitle1,
                        color = Black,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // 네이버 지도 영역 (하단 네비게이션 바 없음)
            NaverMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    isZoomControlEnabled = false,
                    isLocationButtonEnabled = locationSource != null,
                ),
                locationSource = locationSource,
                properties = MapProperties(
                    locationTrackingMode = LocationTrackingMode.NoFollow,
                ),
            ) {
                // 마커 클러스터링 적용
                Clustering(
                    items = clusterItems,
                    // [성능 최적화 2] 줌 레벨에 따른 동적 클러스터링 반경 설정 (저배율에서 노드 개수 대폭 감소)
                    thresholdStrategy = {
                        val currentZoom = cameraPositionState.position.zoom
                        when {
                            currentZoom < 11.0 -> 70.0  // 서울 전체: 넓게 묶어 노드 수 최소화
                            currentZoom < 13.0 -> 50.0  // 구 단위
                            currentZoom < 15.0 -> 35.0  // 동 단위
                            else -> 20.0                // 상세 확대
                        }
                    },
                    // 클러스터 뱃지 디자인 (원형 민트색)
                    clusterContent = { cluster ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Primary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${cluster.size}",
                                color = White,
                                style = EatssuTheme.typography.body2,
                            )
                        }
                    },
                    // 개별 마커 디자인 (원형 마커 아이콘 + 줌 레벨에 따른 텍스트 노출)
                    leafContent = { leaf ->
                        val store = leaf.tag as? GoodPriceStore ?: return@Clustering

                        // 마커 아이콘 분기:
                        // 1. 전체, 한식, 일식, 양식, 중식: 수저 아이콘 통일 (ic_map_marker_restaurant)
                        // 2. 베이커리: 새로 제작한 빵 아이콘 (ic_map_marker_bakery)
                        // 3. 기타: 기존 카페 아이콘 사용 (ic_map_marker_cafe)
                        val iconRes = when (store.category) {
                            GoodPriceCategory.BAKERY -> R.drawable.ic_map_marker_bakery
                            GoodPriceCategory.ETC -> R.drawable.ic_map_marker_cafe
                            else -> R.drawable.ic_map_marker_restaurant
                        }

                        val currentZoom = cameraPositionState.position.zoom
                        // [성능 최적화 3] 저배율(줌 < 12.5)에서는 텍스트 렌더링 비용을 절감하고 아이콘만 가볍게 노출
                        val showTitle = currentZoom >= 12.5

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                            )
                            if (showTitle) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = store.storeName,
                                    style = EatssuTheme.typography.caption3.copy(fontWeight = FontWeight.Bold),
                                    color = Black,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    },
                    // 클러스터 클릭 시 부드럽게 확대
                    onClickCluster = { cluster, _ ->
                        animateCameraTo(cluster.position, cameraPositionState.position.zoom)
                        true
                    },
                    // 개별 마커 클릭 시 업소 상세 바텀시트 호출
                    onClickLeaf = { leaf, _ ->
                        val store = leaf.tag as? GoodPriceStore ?: return@Clustering true
                        onStoreClick(store)
                        true
                    },
                )
            }

            // 상단 카테고리 필터 바 (지도 상단에 가로 스크롤 형태로 오버레이)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
            ) {
                GoodPriceFilterRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                )
            }

            // 로딩 인디케이터
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary,
                )
            }

            // 하단 에러 스낵바 (업소 정보/장소 불러오기 실패 시 노출)
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            ) {
                errorMessage?.let { message ->
                    EatSsuSnackbar(
                        message = message,
                        type = EatSsuSnackbarType.Danger,
                    )
                }
            }

            // 업소 상세 바텀시트
            uiState.selectedStoreDetail?.let { detail ->
                GoodPriceStoreBottomSheet(
                    storeDetail = detail,
                    onDismiss = onDismissBottomSheet,
                )
            }
        }
    }
}

// 클러스터링 키 클래스
private data class GoodPriceClusterKey(
    val id: Long,
    private val latLng: LatLng,
) : ClusteringKey {
    override fun getPosition() = latLng
}
