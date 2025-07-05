package com.eatssu.android.presentation.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray300
import com.eatssu.android.presentation.compose.ui.theme.Gray600
import com.eatssu.android.presentation.compose.ui.theme.Primary
import com.eatssu.android.presentation.compose.ui.theme.White
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.NaverMap

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapFragmentComposeView() {
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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            // 지도
            NaverMap(modifier = Modifier.fillMaxSize())

            MapFilterToggle(
                selected = FilterType.All,
                onSelectedChange = { /* TODO: 필터 변경 처리 */ },
                modifier = Modifier.padding(top = 12.dp)
            )

            // 👇 FAB 위치 수동 지정 (우측 상단)
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                shape = CircleShape,
                modifier = Modifier
                    .padding(top = 12.dp, end = 16.dp)
                    .border(width = 1.dp, color = Gray300, shape = CircleShape)
                    .size(40.dp)
                    .align(Alignment.TopEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_like),
                    contentDescription = "좋아요",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


/** 토글 */

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