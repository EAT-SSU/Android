package com.eatssu.android.presentation.widget.ui

import EatSsuButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.widget.ui.component.EatSsuRadioButtonGroup
import timber.log.Timber

@Composable
fun WidgetSettingScreen(
    modifier: Modifier = Modifier,
    onConfirm: (Int) -> Unit = {}
) {
    val restaurantOptions = remember { listOf("학생 식당", "도담 식당", "기숙사 식당") }
    var selectedRestaurant by remember { mutableStateOf(restaurantOptions[0]) } // State for this screen

    LaunchedEffect(selectedRestaurant) {
        println("WidgetSettingScreen: Current selectedRestaurant is $selectedRestaurant")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "확인하고 싶은 식당을 선택하세요.",
            style = EatssuTheme.typography.body2,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        EatSsuRadioButtonGroup(
            options = restaurantOptions,
            selectedOption = selectedRestaurant,
            onOptionSelected = { newSelection ->
                Timber.d("Selected: $newSelection")
                selectedRestaurant = newSelection
            }
        )


        EatSsuButton(text = "선택하기",
            onClick = { onConfirm(restaurantOptions.indexOf(selectedRestaurant)) })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRestaurantSelectionScreen() {
    EatssuTheme {
        WidgetSettingScreen()
    }
}