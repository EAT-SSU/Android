package com.eatssu.android.presentation.widget.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.widget.ui.theme.EatSSUAndroidTheme

@Composable
fun AddWidgetItem(
    @DrawableRes previewImage: Int,
    widgetName: String,
    widgetSize: String,
    onClick: () -> Unit,
) {
    EatSSUAndroidTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.White)
                .clickable(onClick = onClick)
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = previewImage),
                modifier = Modifier.shadow(
                    elevation = 16.dp,
                    spotColor = Color(0xFF2C2C2C),
                    ambientColor = Color(0xFF2C2C2C)
                ),
                contentDescription = "add widget item"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = widgetName,
                style = typography.bodyLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = widgetSize,
                style = typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
private fun AddWidgetItemPreview() {
    AddWidgetItem(
        previewImage = R.drawable.img_widget_big,
        widgetName = "Widget Name",
        widgetSize = "Widget Size",
        onClick = {}
    )
}