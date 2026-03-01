package com.eatssu.android.presentation.widget.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.util.asString
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.Restaurant
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuRadioButtonGroup
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun WidgetSettingScreen(
    modifier: Modifier = Modifier,
    restaurantOptionList: List<String>,
    selectedRestaurant: String,
    onSelectRestaurant: (String) -> Unit,
    onConfirm: (Restaurant) -> Unit = {},
    onBack: () -> Unit = {}  // 뒤로가기 동작을 위한 람다 추가
) {
    // onClick 람다에서 LocalContext 접근이 불가하므로 Composable 레벨에서 미리 매핑 생성
    val restaurantDisplayNameMap = Restaurant.getVariableRestaurantList()
        .associateBy { it.toUiText().asString() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.title_widget_setting),
                onBack = onBack
            )
        },
        content = { innerPadding -> // innerPadding 값을 받습니다.
            Column(
                modifier = Modifier
                    .padding(innerPadding) // Scafffold 패딩 적용
                    .fillMaxSize()
                    .padding(horizontal = 24.dp) // 이후에 추가적인 패딩 적용
            ) {
                Text(
                    text = stringResource(R.string.widget_select_restaurant),
                    style = EatssuTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                EatSsuRadioButtonGroup(
                    options = restaurantOptionList,
                    selectedOption = selectedRestaurant,
                    onOptionSelected = { onSelectRestaurant(it) }
                )

                Spacer(modifier = Modifier.weight(1f))

                EatSsuButton(
                    modifier = Modifier.padding(bottom = 74.dp),
                    text = stringResource(R.string.widget_select),
                    onClick = {
                        val selectedRestaurantEnum = restaurantDisplayNameMap[selectedRestaurant]
                            ?: Restaurant.HAKSIK

                        onConfirm(selectedRestaurantEnum)
                        EventLogger.addWidget(selectedRestaurantEnum)
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewWidgetSettingScreen() {
    EatssuTheme {
        val restaurantOptionList = listOf("학생 식당", "도담 식당", "기숙사 식당", "FACULTY(교직원 전용)")
        var selectedRestaurant by remember { mutableStateOf(restaurantOptionList[0]) }

        WidgetSettingScreen(
            restaurantOptionList = restaurantOptionList,
            selectedRestaurant = selectedRestaurant,
            onSelectRestaurant = { selectedRestaurant = it },
            onBack = {}
        )
    }
}