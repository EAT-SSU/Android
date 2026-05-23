package com.eatssu.design_system.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.CheckedColor
import com.eatssu.design_system.theme.Gray400

@Composable
fun EatSsuRadioCheckBox(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = com.eatssu.design_system.theme.EatssuTheme.typography.body2
        )

        Spacer(Modifier.weight(1f))
        if (!isSelected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(width = 2.dp, shape = CircleShape, color = Gray400)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(width = 5.dp, shape = CircleShape, color = CheckedColor)
            )
        }
    }
}

@Composable
fun EatSsuRadioCheckBoxGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(options, key = { it }) { option ->
            val isSelected = option == selectedOption
            EatSsuRadioCheckBox(
                text = option,
                isSelected = isSelected,
                onSelect = { onOptionSelected(option) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
@Preview
fun LanguageSelectionPreview() {
    val languages = listOf("한국어", "English", "JP")

    com.eatssu.design_system.theme.EatssuTheme {
        EatSsuRadioCheckBoxGroup(
            options = languages,
            selectedOption = languages[0],
            onOptionSelected = {}
        )
    }
}