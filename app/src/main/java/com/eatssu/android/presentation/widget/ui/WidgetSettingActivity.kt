package com.eatssu.android.presentation.widget.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.eatssu.android.presentation.widget.medium.TodayMealReceiver
import com.eatssu.android.presentation.widget.small.TodayMealSmallReceiver
import com.eatssu.android.presentation.widget.ui.theme.EatSSUAndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MyWidgetConfigActivity : ComponentActivity() {
    private val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by mutableStateOf(options[0])

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

//                    RadioButtonScreen()

            }
        }
    }
}

//    private suspend fun updateWidget() {
//        val appWidgetManager = AppWidgetManager.getInstance(this)
////        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, TodayMealWidget::class.java))
////
////        ids.forEach { id ->
////            TodayMealWidget().update(this, AppWidgetId(id))
////        }
//        val glanceIds = GlanceAppWidgetManager(this).getGlanceIds(TodayMealWidget::class.java)
//
//        glanceIds.forEach { glanceId ->
//            TodayMealWidget().update(this, glanceId)
//        }
//
//        setResult(
//            Activity.RESULT_OK,
//            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, glanceIds.first().toString())
//        )
//        finish()
//
//    }
//
//    private fun savePreference(option: String) {
//        val prefs = getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
//        prefs.edit().putString("selected_option", option).apply()
//    }
//}


//@Composable
//fun RadioButtonScreen() {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimary
//                ),
//                title = {
//                    Text("위젯 설정")
//                }
//            )
//        },
//        content = { }
//    ) {
//    val selectedOption = remember { mutableStateOf("Option 1") }
//
//    Column {
//        Text("Choose an option:")
//        RadioButtonOption(
//            label = "Option 1",
//            isSelected = selectedOption.value == "Option 1",
//            onClick = { selectedOption.value = "Option 1" }
//        )
//        RadioButtonOption(
//            label = "Option 2",
//            isSelected = selectedOption.value == "Option 2",
//            onClick = { selectedOption.value = "Option 2" }
//        )
//        RadioButtonOption(
//            label = "Option 3",
//            isSelected = selectedOption.value == "Option 3",
//            onClick = { selectedOption.value = "Option 3" }
//        )
//    }
//}
//
//@Composable
//fun RadioButtonOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        RadioButton(
//            selected = isSelected,
//            onClick = onClick,
//            colors = RadioButtonDefaults.colors(
//                selectedColor = androidx.compose.ui.graphics.Color.Blue,
//                unselectedColor = androidx.compose.ui.graphics.Color.Gray
//            )
//        )
//        Text(text = label)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewRadioButtonScreen() {
//    RadioButtonScreen()
//}

enum class WidgetType(val value: String) {
    MEAL_SMALL("meal_small"),
    MEAL_MEDIUM("meal_medium"),
}

enum class Widget(
//    @DrawableRes val previewImage: Int,
    val widgetName: String,
    val widgetSize: String,
    val widgetType: WidgetType,
    val widgetReceiverClass: Class<out GlanceAppWidgetReceiver>,
) {
    SMALL_MEAL(
//        previewImage = R.drawable.img_widget_small,
        widgetName = "급식",
        widgetSize = "2 x 2",
        widgetType = WidgetType.MEAL_SMALL,
        widgetReceiverClass = TodayMealSmallReceiver::class.java
    ),
    MEDIUM_MEAL(
//        previewImage = R.drawable.img_widget_big,
        widgetName = "시간표",
        widgetSize = "4 X 2",
        widgetType = WidgetType.MEAL_MEDIUM,
        widgetReceiverClass = TodayMealReceiver::class.java
    ),
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingScreen(
    context: Context = LocalContext.current,  // 기본값 설정
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {

//    val context = LocalContext.current

    EatSSUAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                TopAppBar(title = { Text("위젯 설정") })
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                itemsIndexed(Widget.values()) { _, item ->
                    AddWidgetItem(
//                        previewImage = item.previewImage,
                        widgetName = item.widgetName,
                        widgetSize = item.widgetSize,
                    ) {
                        requestWidgetPin(context, coroutineScope, item)
                    }
                }


            }

        }

    }

}


// UI에서 호출하는 별도 비UI 함수
fun requestWidgetPin(context: Context, coroutineScope: CoroutineScope, item: Widget) {
    coroutineScope.launch {
        GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
            receiver = item.widgetReceiverClass,
            successCallback = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, WidgetSuccessReceiver::class.java).apply {
                    putExtra("widgetType", item.widgetType.value)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun WidgetSettingScreenPreview() {
    EatSSUAndroidTheme {
        WidgetSettingScreen(
            context = LocalContext.current,
            coroutineScope = rememberCoroutineScope()
        )
    }
}
