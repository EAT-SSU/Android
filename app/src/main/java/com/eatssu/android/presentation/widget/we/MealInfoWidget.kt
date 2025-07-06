package com.eatssu.android.presentation.widget.we

//import com.eatssu.android.presentation.widget.theme.EATSSUWidgetColorScheme
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.eatssu.android.R
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.presentation.widget.we.theme.EATSSUWidgetColorScheme
import com.eatssu.android.presentation.widget.we.util.launchApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber


class MealWidget : GlanceAppWidget() {
    override val stateDefinition = MealInfoStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            LaunchedEffect(key1 = Unit) {
                MealWorker.enqueue(context)
            }

            // GlanceId -> appWidgetId 매핑
            val manager = androidx.glance.appwidget.GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)
            // DataStore에서 식당 정보 로드
            val restaurant = runBlocking {
                MealWidgetConfigureActivity.loadRestaurantPref(context, appWidgetId)
            }

            GlanceTheme(colors = EATSSUWidgetColorScheme.colors) {
                when (val state = currentState<MealInfo>()) {
                    is MealInfo.Available -> {
                        if (state.mealList.isNotEmpty()) {
                            MealWidgetContent(
                                mealTime = state.mealTime,
                                mealList = state.mealList,
                                restaurant = state.restaurant,
                            )
                        } else {
                            MealWidgetError(
                                mealTime = state.mealTime,
                                restaurant = state.restaurant,
                                text = "오늘의 메뉴가 없습니다."
                            )

                        }
                    }

                    is MealInfo.Loading -> {
                        MealWidgetError(
                            restaurant = Restaurant.DODAM,
                            mealTime = "점심",
                            text = "로딩 중"
                        )
                    }

                    is MealInfo.Unavailable -> {
                        MealWidgetError(
                            restaurant = Restaurant.DODAM,
                            mealTime = "점심",
                            text = "네트워크 연결 상태를 확인해주세요."
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun MealWidgetContent(
        mealTime: String,
        mealList: List<List<String>>,
        restaurant: Restaurant,
    ) {
        val context = LocalContext.current

        MealWidgetScaffold(
            mealTime = mealTime,
            restaurant = restaurant,
            onLeftArrowClick = { changeRestaurantAndUpdateWidget(context) },
            onRightArrowClick = { /* TODO */ }
        ) {
            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .cornerRadius(10.dp)
                    .background(GlanceTheme.colors.onBackground)
                    .clickable {
                        Timber.d("위젯 클릭")
                        context.launchApp()
                    },
            ) {
                itemsIndexed(mealList) { index, group ->
                    val groupText = group.joinToString(" + ")
                    Column {
                        Text(
                            text = groupText,
                        )
                        if (mealList.lastIndex != index) {
                            Spacer(modifier = GlanceModifier.height(8.dp)) // 그룹 간 간격
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MealWidgetError(
        mealTime: String,
        restaurant: Restaurant,
        text: String
    ) {
        val context = LocalContext.current

        MealWidgetScaffold(
            mealTime = mealTime,
            restaurant = restaurant,
            onLeftArrowClick = { changeRestaurantAndUpdateWidget(context) },
            onRightArrowClick = { /* TODO */ }
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .cornerRadius(10.dp)
                    .background(GlanceTheme.colors.onBackground)
            ) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        modifier = GlanceModifier.size(20.dp)
                            .padding(bottom = 6.dp),
                        provider = ImageProvider(R.drawable.ic_alert_circle),
                        contentDescription = "alert"
                    )
                    Text(
                        text,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                    )
                }
            }
        }
    }

    @Composable
    fun MealWidgetScaffold(
        mealTime: String?,
        restaurant: Restaurant,
        onLeftArrowClick: () -> Unit,
        onRightArrowClick: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.onPrimary)
                .padding(16.dp)
                .cornerRadius(20.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = GlanceModifier.size(height = 14.dp, width = 43.dp),
                    provider = ImageProvider(R.drawable.img_new_logo_primary),
                    contentDescription = "Logo",
                )
                Spacer(modifier = GlanceModifier.size(8.dp))
                if (mealTime != null) {
                    Text(
                        mealTime,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = GlanceModifier.size(18.dp).clickable { onLeftArrowClick() },
                        provider = ImageProvider(R.drawable.ic_arrow_left),
                        contentDescription = "left"
                    )
                    Text(
                        restaurant.displayName,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                    )
                    Image(
                        modifier = GlanceModifier.size(18.dp).clickable { onRightArrowClick() },
                        provider = ImageProvider(R.drawable.ic_arrow_right),
                        contentDescription = "right"
                    )
                }
            }
            Spacer(modifier = GlanceModifier.height(12.dp))
            content()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreview() {
        MealWidgetContent("저녁", listOf(listOf("밥", "국", "반찬", "음료")), Restaurant.DODAM)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreviewError() {
        MealWidgetError("저녁", Restaurant.DODAM, "에러임")
    }

}

class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MealWidget()
}

// Helper function to cycle restaurants
fun getNextRestaurant(current: Restaurant): Restaurant {
    return when (current) {
        Restaurant.HAKSIK -> Restaurant.DODAM
        Restaurant.DODAM -> Restaurant.DORMITORY
        Restaurant.DORMITORY -> Restaurant.HAKSIK
        else -> Restaurant.HAKSIK
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun changeRestaurantAndUpdateWidget(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetComponent = ComponentName(context, MealWidgetReceiver::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
        val appWidgetId = appWidgetIds.firstOrNull() ?: return@launch
        val current = MealWidgetConfigureActivity.loadRestaurantPref(context, appWidgetId)
        val next = getNextRestaurant(current)
        Timber.d(next.displayName)
        MealWidgetConfigureActivity.saveRestaurantPref(context, appWidgetId, next)
        MealWidget().updateAll(context)
        MealWorker.enqueue(context)
    }
}
