package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.presentation.map.hasFestivalPartnership
import com.eatssu.android.presentation.map.iconRes
import com.eatssu.common.enums.StoreType

private val FestivalWine = Color(0xFF880A19)

@Composable
internal fun PartnershipMarkerIcon(
    partnership: Partnership,
    modifier: Modifier = Modifier,
) {
    if (partnership.hasFestivalPartnership) {
        Box(
            modifier = modifier
                .size(20.dp)
                .background(FestivalWine, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = partnership.restaurantType.iconRes),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
        }
    } else {
        val iconRes = when (partnership.restaurantType) {
            StoreType.CAFE -> R.drawable.ic_map_marker_cafe
            StoreType.PUB -> R.drawable.ic_map_marker_pub
            else -> R.drawable.ic_map_marker_restaurant
        }

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = modifier.size(20.dp),
        )
    }
}
