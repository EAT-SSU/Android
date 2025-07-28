package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.Black
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray200
import com.eatssu.android.presentation.compose.ui.theme.Gray400
import com.eatssu.android.presentation.compose.ui.theme.Gray500
import com.eatssu.android.presentation.compose.ui.theme.Gray600
import com.eatssu.android.presentation.compose.ui.theme.White
import com.eatssu.android.presentation.map.model.FavoritePartnership

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePartnershipBottomSheet(
    favoriteList: List<FavoritePartnership>,
    onDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(bottom = 40.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(30.dp)
                    .height(2.dp)
                    .background(Gray400, RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 상단 타이틀
            Text(
                text = stringResource(R.string.favorite_partnership),
                style = EatssuTheme.typography.subtitle1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(favoriteList) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = item.storeName,
                                    style = EatssuTheme.typography.body1,
                                    color = Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.category,
                                    style = EatssuTheme.typography.caption3,
                                    color = Gray500
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.period,
                                style = EatssuTheme.typography.caption2,
                                color = Gray600
                            )

                        }

//                        Image(
//                            painter = painterResource(id = R.drawable.ic_like),
//                            contentDescription = "찜한 가게",
//                            modifier = Modifier
//                                .size(32.dp)
//                                .padding(start = 8.dp)
//                        )
                    }

                    // 구분선
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Gray200)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteAffiliationBottomSheetPreview() {
    val dummyList = listOf(
        FavoritePartnership("카페봄봄", "카페", "09.01 - 12.31"),
        FavoritePartnership("현선이네", "음식점", "09.01 - 12.31"),
        FavoritePartnership("청년다방", "카페", "09.01 - 12.31")
    )

    EatssuTheme {
        Surface {
            FavoritePartnershipBottomSheet(
                favoriteList = dummyList
            )
        }
    }
}
