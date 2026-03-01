package com.eatssu.android.presentation.cafeteria.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eatssu.android.R
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    restaurant: Restaurant,
    infoViewModel: InfoViewModel,
    onDismiss: () -> Unit,
) {
    val infoState = produceState<RestaurantInfo?>(initialValue = null, restaurant) {
        value = infoViewModel.getRestaurantInfo(restaurant)
    }
    val info = infoState.value

    LogScreenView(ScreenId.HOME_INFO)

    InfoBottomSheetContent(
        info = info,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InfoBottomSheetContent(
    info: RestaurantInfo?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(30.dp)
                    .height(2.dp)
                    .background(Gray400),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // padding="16dp" on root
                .padding(bottom = 70.dp), // marginBottom=70dp
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (info != null) {
                Text(
                    text = info.name,
                    style = EatssuTheme.typography.h1,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp),
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    thickness = 1.dp,
                    color = Gray200,
                )
                InfoRow(
                    label = stringResource(R.string.location),
                    value = info.location,
                )
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(5.dp),
                ) {
                    AsyncImage(
                        model = info.image,
                        contentDescription = info.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.height(30.dp))
                InfoRow(
                    label = stringResource(R.string.time),
                    value = info.time,
                )
                Spacer(Modifier.height(30.dp))
                InfoRow(
                    label = stringResource(R.string.etc),
                    value = info.etc,
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = EatssuTheme.typography.subtitle2,
            color = Color.Black,
            modifier = Modifier.weight(3f),
        )
        Text(
            text = value,
            style = EatssuTheme.typography.body3,
            color = Color.Black,
            modifier = Modifier.weight(7f),
            textAlign = TextAlign.End,
        )
    }
}

@Preview
@Composable
private fun InfoBottomSheetContentPreview() {
    EatssuTheme {
        InfoBottomSheetContent(
            info = RestaurantInfo(
                enum = Restaurant.HAKSIK,
                name = "학생식당",
                location = "학생회관 1층",
                image = "",
                time = "08:00 - 18:00",
                etc = "카드 결제 가능",
            ),
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun InfoRowPreview() {
    EatssuTheme {
        InfoRow(
            label = "위치",
            value = "학생회관 1층",
        )
    }
}
