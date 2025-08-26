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
            // GlanceId -> appWidgetId 매핑
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)

            // DataStore에서 식당 정보 로드 - glanceId를 사용하여 정확한 식당 정보 가져오기
            val restaurant = runBlocking {
                val fileKey = "appWidget-${appWidgetId}"
                Timber.d("loadRestaurantByFileKey 호출: fileKey = '$fileKey', appWidgetId = $appWidgetId")
                val result = WidgetSettingActivity.loadRestaurantByFileKey(
                    context.applicationContext,
                    fileKey
                )
                Timber.d("loadRestaurantByFileKey 결과: $result")
                result
            }
            Timber.d("load2 ${restaurant?.name ?: "null"} for glanceId: ${id}")

            LaunchedEffect(key1 = Unit) {
                MealWorker.enqueue(context)

                // 더 오래 기다린 후 저장된 식당 정보가 있는지 확인
                kotlinx.coroutines.delay(2000)
                val savedRestaurant = WidgetSettingActivity.loadRestaurantByFileKey(
                    context.applicationContext,
                    "appWidget-${appWidgetId}"
                )
                if (savedRestaurant != null) {
                    Timber.d("LaunchedEffect: 저장된 식당 정보 발견 - ${savedRestaurant.name}, 위젯 강제 업데이트")
                    // 위젯을 강제로 업데이트하여 저장된 식당 정보가 반영되도록 함
                    MealWidget().update(context, id)
                } else {
                    Timber.d("LaunchedEffect: 저장된 식당 정보 없음")
                }
            }

            GlanceTheme(colors = EATSSUWidgetColorScheme.colors) {
                if (restaurant != null) {
                    // 저장된 식당 정보가 있으면 해당 식당의 데이터 표시
                    when (val state = currentState<WidgetMealInfo>()) {
                        is WidgetMealInfo.Available -> {
                            if (state.mealList.isNotEmpty()) {
                                MealWidgetContent(
                                    mealTime = state.mealTime,
                                    mealList = state.mealList,
                                    restaurant = restaurant.displayName,
                                    glanceId = id,
                                )
                            } else {
                                MealWidgetError(
                                    mealTime = state.mealTime,
                                    restaurant = restaurant.displayName,
                                    text = "오늘의 메뉴가 없습니다.",
                                    glanceId = id,
                                )
                            }
                        }

                        is WidgetMealInfo.Loading -> {
                            // Loading 상태일 때도 저장된 식당 정보 표시
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
                } else {
                    // 저장된 식당 정보가 없으면 설정 필요 메시지 표시
                    MealWidgetError(
                        restaurant = "설정 필요",
                        mealTime = "점심",
                        text = "위젯 설정에서 식당을 선택해주세요.",
                        glanceId = id,
                    )
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



