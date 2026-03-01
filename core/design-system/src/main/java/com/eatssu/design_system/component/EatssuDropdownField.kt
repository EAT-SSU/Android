package com.eatssu.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eatssu.design_system.R
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray200
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Gray700
import com.eatssu.design_system.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EatssuDropdownField(
    selectedText: String,
    options: List<String>,
    onOptionSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier,
    ) {
        // 표시 영역: shape_text_field_small 스타일
        Row(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .height(52.dp)
                .background(Gray100, RoundedCornerShape(12.dp))
                .border(1.dp, Gray200, RoundedCornerShape(12.dp))
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedText.ifEmpty { placeholder },
                style = EatssuTheme.typography.body2,
                color = if (selectedText.isEmpty()) Gray400 else Gray700,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Gray600,
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(White, RoundedCornerShape(12.dp))
                .border(1.dp, Gray200, RoundedCornerShape(12.dp)),
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = EatssuTheme.typography.body2.copy(fontSize = 16.sp),
                        )
                    },
                    onClick = {
                        onOptionSelected(index, option)
                        expanded = false
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .background(White), // item_dropdown height
                    colors = MenuDefaults.itemColors(textColor = Gray700),
                    contentPadding = PaddingValues(start = 15.dp),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun EatssuDropdownFieldPreview() {
    EatssuTheme {
        EatssuDropdownField(
            selectedText = "컴퓨터학부",
            options = listOf("컴퓨터학부", "AI융합학부", "전자정보공학부"),
            onOptionSelected = { _, _ -> },
            placeholder = "학과",
        )
    }
}
