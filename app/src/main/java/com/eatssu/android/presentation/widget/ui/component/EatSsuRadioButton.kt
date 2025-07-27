package com.eatssu.android.presentation.widget.ui.component // Replace with your actual package name


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme

@Composable
fun EatSsuRadioButton(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(15.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = EatssuTheme.typography.body2
        )
    }
}

@Composable
fun EatSsuRadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            EatSsuRadioButton(
                text = option,
                isSelected = isSelected,
                onSelect = { onOptionSelected(option) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
@Preview
fun RestaurantSelectionPreview() {
    val restaurantOptions = listOf("학생 식당", "도담 식당", "기숙사 식당")

    EatssuTheme {
        EatSsuRadioButtonGroup(
            options = restaurantOptions,
            selectedOption = restaurantOptions[0],
            onOptionSelected = {}
        )
    }
}