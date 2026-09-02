package com.eatssu.android.presentation.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.presentation.map.iconRes
import com.eatssu.android.presentation.util.TrackScreenViewEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.StoreType
import com.eatssu.design_system.component.DelayedLoadingIndicator
import com.eatssu.design_system.component.EatSsuSnackbar
import com.eatssu.design_system.component.EatSsuSnackbarType
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Error
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray500
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary
import com.eatssu.design_system.theme.White
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val MENU_PAGE = 0
private const val PARTNERSHIP_PAGE = 1

@Composable
fun FavoriteRoute(
    viewModel: FavoriteViewModel,
    onBackToMap: () -> Unit,
    onPartnershipClick: (PartnershipRestaurant) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoriteScreen(
        uiState = uiState,
        onBackToMap = onBackToMap,
        onStoreTypeSelected = viewModel::selectStoreType,
        onPartnershipClick = onPartnershipClick,
        onRemoveFavorite = viewModel::removeFavorite,
        onRemoveFavorites = viewModel::removeFavorites,
        onRestoreFavorites = viewModel::restoreFavorites,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteScreen(
    uiState: UiState<FavoriteState>,
    onBackToMap: () -> Unit,
    onStoreTypeSelected: (StoreType?) -> Unit,
    onPartnershipClick: (PartnershipRestaurant) -> Unit,
    onRemoveFavorite: (Int) -> Unit = {},
    onRemoveFavorites: (Set<Int>) -> Unit = {},
    onRestoreFavorites: (List<FavoritePartnershipItem>) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = PARTNERSHIP_PAGE,
        pageCount = { 2 },
    )
    val scope = rememberCoroutineScope()
    var isEditMode by remember { mutableStateOf(false) }
    var selectedPartnershipIds by remember { mutableStateOf(setOf<Int>()) }
    var lastDeletedPartnerships by remember {
        mutableStateOf<List<FavoritePartnershipItem>>(
            emptyList()
        )
    }
    var showDeleteSnackbar by remember { mutableStateOf(false) }
    var snackbarJob by remember { mutableStateOf<Job?>(null) }

    TrackScreenViewEvent(ScreenId.MAP_FAVORITE)

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
                        text = stringResource(R.string.favorite_title),
                        style = EatssuTheme.typography.subtitle1,
                        color = Gray700,
                        textAlign = TextAlign.Center,
                    )

                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                isEditMode = false
                                selectedPartnershipIds = emptySet()
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_left),
                                contentDescription = stringResource(R.string.nav_back),
                                tint = Gray500,
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = dimensionResource(R.dimen.bottom_nav_height)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isEditMode) {
                    FavoriteEditContent(
                        uiState = uiState,
                        selectedPartnershipIds = selectedPartnershipIds,
                        onToggleSelect = { id ->
                            selectedPartnershipIds = if (id in selectedPartnershipIds) {
                                selectedPartnershipIds - id
                            } else {
                                selectedPartnershipIds + id
                            }
                        },
                        onToggleSelectAll = { allIds, isAllSelected ->
                            selectedPartnershipIds = if (isAllSelected) emptySet() else allIds
                        },
                        onDelete = {
                            val deletedItems =
                                (uiState as? UiState.Success)?.data?.partnerships?.filter { it.partnershipId in selectedPartnershipIds }
                                    .orEmpty()
                            onRemoveFavorites(selectedPartnershipIds)
                            if (deletedItems.isNotEmpty()) {
                                lastDeletedPartnerships = deletedItems
                                showDeleteSnackbar = true
                                snackbarJob?.cancel()
                                snackbarJob = scope.launch {
                                    delay(3500)
                                    showDeleteSnackbar = false
                                }
                            }
                            selectedPartnershipIds = emptySet()
                            isEditMode = false
                        },
                    )
                } else {
                    FavoriteTabs(
                        selectedPage = pagerState.currentPage,
                        onPageSelected = { page ->
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    page
                                )
                            }
                        },
                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        if (page == PARTNERSHIP_PAGE) {
                            FavoritePartnershipContent(
                                uiState = uiState,
                                onStoreTypeSelected = onStoreTypeSelected,
                                onPartnershipClick = onPartnershipClick,
                                onRemoveFavorite = { id ->
                                    val deletedItem =
                                        (uiState as? UiState.Success)?.data?.partnerships?.find { it.partnershipId == id }
                                    onRemoveFavorite(id)
                                    if (deletedItem != null) {
                                        lastDeletedPartnerships = listOf(deletedItem)
                                        showDeleteSnackbar = true
                                        snackbarJob?.cancel()
                                        snackbarJob = scope.launch {
                                            delay(3500)
                                            showDeleteSnackbar = false
                                        }
                                    }
                                },
                                onEditClick = {
                                    isEditMode = true
                                    selectedPartnershipIds = emptySet()
                                },
                            )
                        } else {
                            Spacer(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showDeleteSnackbar,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
            ) {
                EatSsuSnackbar(
                    message = stringResource(R.string.favorite_deleted_snackbar),
                    actionLabel = stringResource(R.string.favorite_undo),
                    onActionClick = {
                        snackbarJob?.cancel()
                        showDeleteSnackbar = false
                        onRestoreFavorites(lastDeletedPartnerships)
                        lastDeletedPartnerships = emptyList()
                    },
                    type = EatSsuSnackbarType.Success,
                )
            }
        }
    }
}

@Composable
private fun FavoriteEditContent(
    uiState: UiState<FavoriteState>,
    selectedPartnershipIds: Set<Int>,
    onToggleSelect: (Int) -> Unit,
    onToggleSelectAll: (Set<Int>, Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    when (uiState) {
        UiState.Init, UiState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        UiState.Error -> FavoriteEmptyContent()
        is UiState.Success -> {
            val partnerships = uiState.data.filteredPartnerships
            if (partnerships.isEmpty()) {
                FavoriteEmptyContent()
            } else {
                val allIds = partnerships.map { it.partnershipId }.toSet()
                val isAllSelected =
                    allIds.isNotEmpty() && selectedPartnershipIds.size == allIds.size

                Column(modifier = Modifier.fillMaxSize()) {
                    FavoriteSelectAllRow(
                        isAllSelected = isAllSelected,
                        onToggleSelectAll = { onToggleSelectAll(allIds, isAllSelected) },
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        itemsIndexed(
                            items = partnerships,
                            key = { index, item -> "${item.partnershipId}_$index" },
                        ) { _, item ->
                            val isSelected = item.partnershipId in selectedPartnershipIds
                            FavoriteEditRow(
                                item = item,
                                isSelected = isSelected,
                                onToggle = { onToggleSelect(item.partnershipId) },
                            )
                        }
                    }

                    val count = selectedPartnershipIds.size
                    val isEnabled = count > 0
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isEnabled) Error else Gray200)
                            .clickable(enabled = isEnabled, onClick = onDelete),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (count > 0) {
                                stringResource(R.string.favorite_delete_count, count)
                            } else {
                                stringResource(R.string.favorite_delete)
                            },
                            style = EatssuTheme.typography.button1,
                            color = if (isEnabled) White else Gray400,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteSelectAllRow(
    isAllSelected: Boolean,
    onToggleSelectAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleSelectAll)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularCheckbox(
            checked = isAllSelected,
            onCheckedChange = onToggleSelectAll,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.favorite_select_all),
            style = EatssuTheme.typography.h2,
            color = Color.Black,
        )
    }
}

@Composable
private fun FavoriteEditRow(
    item: FavoritePartnershipItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularCheckbox(
            checked = isSelected,
            onCheckedChange = onToggle,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.storeName,
                    style = EatssuTheme.typography.h2,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.storeType.value,
                    style = EatssuTheme.typography.body3,
                    color = Gray600,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = EatssuTheme.typography.body3,
                color = Gray600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun CircularCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (() -> Unit)? = null,
) {
    val clickableModifier = if (onCheckedChange != null) {
        modifier.clickable(onClick = onCheckedChange)
    } else {
        modifier
    }

    Canvas(
        modifier = clickableModifier.size(24.dp),
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 1.dp.toPx()

        if (checked) {
            drawCircle(
                color = Primary,
                radius = radius,
                center = center,
            )
            val path = Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.50f)
                lineTo(size.width * 0.44f, size.height * 0.66f)
                lineTo(size.width * 0.72f, size.height * 0.36f)
            }
            drawPath(
                path = path,
                color = White,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        } else {
            drawCircle(
                color = White,
                radius = radius,
                center = center,
            )
            drawCircle(
                color = Gray300,
                radius = radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx()),
            )
            val path = Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.50f)
                lineTo(size.width * 0.44f, size.height * 0.66f)
                lineTo(size.width * 0.72f, size.height * 0.36f)
            }
            drawPath(
                path = path,
                color = Gray300,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}

@Composable
private fun FavoriteTabs(
    selectedPage: Int,
    onPageSelected: (Int) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf(
            MENU_PAGE to stringResource(R.string.favorite_menu_tab),
            PARTNERSHIP_PAGE to stringResource(R.string.favorite_partnership_tab),
        ).forEach { (page, label) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPageSelected(page) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = label,
                    style = EatssuTheme.typography.h2,
                    color = if (selectedPage == page) Primary else Gray400,
                    modifier = Modifier.padding(vertical = 18.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(if (selectedPage == page) Primary else Gray400),
                )
            }
        }
    }
}

@Composable
private fun FavoritePartnershipContent(
    uiState: UiState<FavoriteState>,
    onStoreTypeSelected: (StoreType?) -> Unit,
    onPartnershipClick: (PartnershipRestaurant) -> Unit,
    onRemoveFavorite: (Int) -> Unit,
    onEditClick: () -> Unit,
) {
    when (uiState) {
        UiState.Init, UiState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        UiState.Error -> FavoriteEmptyContent()
        is UiState.Success -> Column(modifier = Modifier.fillMaxSize()) {
            FavoriteFilters(
                selectedStoreType = uiState.data.selectedStoreType,
                onStoreTypeSelected = onStoreTypeSelected,
                onEditClick = onEditClick,
            )

            if (uiState.data.filteredPartnerships.isEmpty()) {
                FavoriteEmptyContent()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = uiState.data.filteredPartnerships,
                        key = { index, item -> "${item.partnershipId}_$index" },
                    ) { _, item ->
                        FavoriteSwipeToDismissItem(
                            item = item,
                            onClick = { item.detail?.let(onPartnershipClick) },
                            onRemoveFavorite = onRemoveFavorite,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteFilters(
    selectedStoreType: StoreType?,
    onStoreTypeSelected: (StoreType?) -> Unit,
    onEditClick: () -> Unit,
) {
    val filters = listOf(
        null to stringResource(R.string.favorite_filter_all),
        StoreType.RESTAURANT to StoreType.RESTAURANT.value,
        StoreType.CAFE to StoreType.CAFE.value,
        StoreType.PUB to StoreType.PUB.value,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { (type, label) ->
            val selected = selectedStoreType == type
            Text(
                text = label,
                style = EatssuTheme.typography.body3,
                fontWeight = if (selected) FontWeight.W500 else FontWeight.W400,
                color = if (selected) Primary else Gray400,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .then(
                        if (selected) Modifier.background(Secondary)
                        else Modifier.border(1.dp, Gray300, RoundedCornerShape(24.dp)),
                    )
                    .clickable { onStoreTypeSelected(type) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.favorite_edit),
            style = EatssuTheme.typography.button2.copy(color = Gray500),
            modifier = Modifier.clickable(onClick = onEditClick),
        )
    }
}

@Composable
private fun FavoriteSwipeToDismissItem(
    item: FavoritePartnershipItem,
    onClick: () -> Unit,
    onRemoveFavorite: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val maxOffsetPx = with(LocalDensity.current) { -72.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.White),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935))
                    .clickable {
                        scope.launch { offsetX.animateTo(0f) }
                        onRemoveFavorite(item.partnershipId)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = stringResource(R.string.button_delete),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxWidth()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value < maxOffsetPx / 2) {
                                    offsetX.animateTo(maxOffsetPx)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset =
                                    (offsetX.value + dragAmount).coerceIn(maxOffsetPx, 0f)
                                offsetX.snapTo(newOffset)
                            }
                        },
                    )
                },
        ) {
            FavoritePartnershipRow(
                item = item,
                onClick = {
                    if (offsetX.value < 0f) {
                        scope.launch { offsetX.animateTo(0f) }
                    } else {
                        onClick()
                    }
                },
            )
        }
    }
}

@Composable
private fun FavoritePartnershipRow(
    item: FavoritePartnershipItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(item.storeType.iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.storeName,
                    style = EatssuTheme.typography.h2,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.storeType.value,
                    style = EatssuTheme.typography.body3,
                    color = Gray600,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = EatssuTheme.typography.body3,
                color = Gray600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = stringResource(R.string.favorite_open_detail),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun FavoriteEmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.favorite_empty_title),
            style = EatssuTheme.typography.h1,
            color = Gray600,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.favorite_empty_description),
            style = EatssuTheme.typography.body2,
            color = Gray600,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteScreenPreview() {
    EatssuTheme {
        FavoriteScreen(
            uiState = UiState.Success(
                FavoriteState(
                    partnerships = listOf(
                        FavoritePartnershipItem(
                            1,
                            "식당 이름",
                            StoreType.RESTAURANT,
                            "학생증 인증하면 음료수 1개 증정"
                        ),
                        FavoritePartnershipItem(2, "카페 이름", StoreType.CAFE, "학생증 인증하면 음료수 1개 증정"),
                    ),
                ),
            ),
            onBackToMap = {},
            onStoreTypeSelected = {},
            onPartnershipClick = {},
        )
    }
}
