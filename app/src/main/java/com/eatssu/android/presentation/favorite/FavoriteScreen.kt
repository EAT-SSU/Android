package com.eatssu.android.presentation.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import com.eatssu.design_system.theme.Secondary
import kotlinx.coroutines.launch

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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteScreen(
    uiState: UiState<FavoriteState>,
    onBackToMap: () -> Unit,
    onStoreTypeSelected: (StoreType?) -> Unit,
    onPartnershipClick: (PartnershipRestaurant) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = PARTNERSHIP_PAGE,
        pageCount = { 2 },
    )
    val scope = rememberCoroutineScope()

    TrackScreenViewEvent(ScreenId.MAP_FAVORITE)

    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.favorite_title),
                onBack = onBackToMap,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = dimensionResource(R.dimen.bottom_nav_height)),
        ) {
            FavoriteTabs(
                selectedPage = pagerState.currentPage,
                onPageSelected = { page -> scope.launch { pagerState.animateScrollToPage(page) } },
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
                    )
                } else {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            }
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
) {
    when (uiState) {
        UiState.Init, UiState.Loading -> DelayedLoadingIndicator(modifier = Modifier.fillMaxSize())
        UiState.Error -> FavoriteEmptyContent()
        is UiState.Success -> Column(modifier = Modifier.fillMaxSize()) {
            FavoriteFilters(
                selectedStoreType = uiState.data.selectedStoreType,
                onStoreTypeSelected = onStoreTypeSelected,
            )

            if (uiState.data.filteredPartnerships.isEmpty()) {
                FavoriteEmptyContent()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = uiState.data.filteredPartnerships,
                        key = FavoritePartnershipItem::partnershipId,
                    ) { item ->
                        FavoritePartnershipRow(
                            item = item,
                            onClick = { item.detail?.let(onPartnershipClick) },
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
    ) {
        filters.forEach { (type, label) ->
            val selected = selectedStoreType == type
            Text(
                text = label,
                style = EatssuTheme.typography.body1,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
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
