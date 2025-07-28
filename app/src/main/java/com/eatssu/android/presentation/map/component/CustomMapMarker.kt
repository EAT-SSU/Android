package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.Black
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray300
import com.eatssu.android.presentation.compose.ui.theme.Primary
import com.eatssu.android.presentation.compose.ui.theme.White

enum class PlaceType(val placeCategory: String, val iconRes: Int) {
    CAFE("카페", R.drawable.ic_map_cafe),
    RESTAURANT("음식점", R.drawable.ic_map_restaurant),
    Alcohol("주점", R.drawable.ic_map_alcohol),
}

@Composable
fun CustomMapMarker(
    type: PlaceType,
    placeName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(White)
            .border(0.5.dp, Gray300, shape = CircleShape)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = type.iconRes),
                contentDescription = placeName,
                modifier = Modifier.size(13.dp)
            )
        }

        Text(
            text = placeName,
            color = Black,
            style = EatssuTheme.typography.caption3,
            modifier = Modifier.padding(start = 2.dp, end = 8.dp)
        )
    }
}

@Composable
@Preview
fun PlaceTagPreview() {
    EatssuTheme {
        Column {
            CustomMapMarker(
                type = PlaceType.CAFE,
                placeName = "카페"
            )
            CustomMapMarker(
                type = PlaceType.RESTAURANT,
                placeName = "THE KONE",
                modifier = Modifier.padding(top = 20.dp)
            )
            CustomMapMarker(
                type = PlaceType.Alcohol,
                placeName = "술집",
                modifier = Modifier.padding(top = 20.dp)
            )
        }

    }
}