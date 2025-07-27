package com.eatssu.android.presentation.widget

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
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
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
import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.presentation.widget.theme.EATSSUWidgetColorScheme
import com.eatssu.android.presentation.widget.ui.WidgetSettingActivity
import com.eatssu.android.presentation.widget.util.launchApp
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
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)
            // DataStore에서 식당 정보 로드
            val restaurant = runBlocking {
                WidgetSettingActivity.loadRestaurantPref(context, appWidgetId)

            }
            Timber.d("load2 ${restaurant.name}")

            GlanceTheme(colors = EATSSUWidgetColorScheme.colors) {
                when (val state = currentState<WidgetMealInfo>()) {
                    is WidgetMealInfo.Available -> {
                        if (state.mealList.isNotEmpty()) {
                            MealWidgetContent(
                                mealTime = state.mealTime,
                                mealList = state.mealList,
                                restaurant = state.restaurant.displayName,
                                glanceId = id,
                            )
                        } else {
                            MealWidgetError(
                                mealTime = state.mealTime,
                                restaurant = state.restaurant.displayName,
                                text = "오늘의 메뉴가 없습니다.",
                                glanceId = id,
                            )
                        }
                    }

                    is WidgetMealInfo.Loading -> {
                        MealWidgetError(
                            restaurant = restaurant.displayName,
                            mealTime = "점심",
                            text = "로딩 중",
                            glanceId = id,
                        )
                    }

                    is WidgetMealInfo.Unavailable -> {
                        MealWidgetError(
                            restaurant = restaurant.displayName,
                            mealTime = "점심",
                            text = "네트워크 연결 상태를 확인해주세요.",
                            glanceId = id,
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
        restaurant: String,
        glanceId: GlanceId? = null,
    ) {
        val context = LocalContext.current

        MealWidgetScaffold(
            context = context,
            mealTime = mealTime,
            restaurantName = restaurant,
            content = {
                LazyColumn(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .cornerRadius(10.dp)
                        .background(GlanceTheme.colors.onBackground)
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
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MealWidgetError(
        mealTime: String,
        restaurant: String,
        text: String,
        glanceId: GlanceId? = null,
    ) {
        val context = LocalContext.current

        MealWidgetScaffold(
            context = context,
            mealTime = mealTime,
            restaurantName = restaurant,
            content = {
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
        )
    }

    @Composable
    fun MealWidgetScaffold(
        context: Context,
        mealTime: String?,
        restaurantName: String,
//        onLeftArrowClick: () -> Unit,
//        onRightArrowClick: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.onPrimary)
                .padding(16.dp)
                .cornerRadius(20.dp)
                .clickable {
                    Timber.d("위젯 클릭")
                    context.launchApp()
                },
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
                    Text(
                        restaurantName,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                    )
                }
            }
            Spacer(modifier = GlanceModifier.height(12.dp))
            content()
            Box(
                modifier = GlanceModifier
                    .clickable {
                        Timber.d("위젯 클릭")
                        context.launchApp()
                    },
            ) {}
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreview() {
        MealWidgetContent("저녁", listOf(listOf("밥", "국", "반찬", "음료")), Restaurant.DODAM.displayName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreviewError() {
        MealWidgetError("저녁", Restaurant.DODAM.displayName, "에러임")
    }

}

class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MealWidget()
}

