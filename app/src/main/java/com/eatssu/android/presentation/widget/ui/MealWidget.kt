package com.eatssu.android.presentation.widget.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.domain.usecase.widget.LoadRestaurantByFileKeyUseCase
import com.eatssu.android.presentation.widget.MealInfoStateDefinition
import com.eatssu.android.presentation.widget.MealWorker
import com.eatssu.android.presentation.widget.theme.EATSSUWidgetColorScheme
import com.eatssu.android.presentation.widget.util.MealTime
import com.eatssu.android.presentation.widget.util.WidgetDataDisplayManager
import com.eatssu.android.presentation.widget.util.launchApp
import com.eatssu.common.enums.Restaurant
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import timber.log.Timber


class MealWidget : GlanceAppWidget() {
    override val stateDefinition = MealInfoStateDefinition

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MealWidgetEntryPoint {
        fun loadRestaurantByFileKeyUseCase(): LoadRestaurantByFileKeyUseCase
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val appContext = context.applicationContext
            val entryPoint =
                EntryPointAccessors.fromApplication(appContext, MealWidgetEntryPoint::class.java)
            val loadRestaurantByFileKeyUseCase = entryPoint.loadRestaurantByFileKeyUseCase()

            // GlanceId -> appWidgetId 매핑
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)

            var restaurant by remember { mutableStateOf<Restaurant?>(null) }

            LaunchedEffect(key1 = Unit) {

                delay(2000) //딜레이 안주면 Init 상태의 위젯이 추가됨.
                val savedRestaurant = loadRestaurantByFileKeyUseCase(
                    "appWidget-${appWidgetId}"
                )
                restaurant = savedRestaurant

                if (savedRestaurant != null) {
                    Timber.d("LaunchedEffect: 저장된 식당 정보 발견 - ${savedRestaurant.name}, 위젯 강제 업데이트")
                    // 위젯을 강제로 업데이트하여 저장된 식당 정보가 반영되도록 함
                    MealWidget().update(context, id)
                } else {
                    Timber.d("LaunchedEffect: 저장된 식당 정보 없음")
                }

                MealWorker.enqueue(context)
            }

            GlanceTheme(colors = EATSSUWidgetColorScheme.colors) {
                if (restaurant != null) {
                    // 저장된 식당 정보가 있으면 해당 식당의 데이터 표시
                    when (val state = currentState<WidgetMealInfo>()) {
                        is WidgetMealInfo.Available -> {
                            // 현재 시간에 맞는 식사 시간의 메뉴를 표시
                            val currentMealTime = WidgetDataDisplayManager.getCurrentMealTime()
                            val (mealTime, mealList) = when (currentMealTime) {

                                MealTime.Morning -> "아침" to state.breakfast
                                MealTime.Lunch -> "점심" to state.lunch
                                MealTime.Dinner -> "저녁" to state.dinner
                            }

                            if (mealList.isNotEmpty()) {
                                MealWidgetContent(
                                    mealTime = mealTime,
                                    mealList = mealList,
                                    restaurant = restaurant?.korean ?: "",
                                    glanceId = id,
                                )
                            } else {
                                MealWidgetError(
                                    mealTime = mealTime,
                                    restaurant = restaurant?.korean ?: "",
                                    text = "오늘의 메뉴가 없습니다.",
                                    glanceId = id,
                                )
                            }
                        }

                        is WidgetMealInfo.Loading -> {
                            // Loading 상태일 때도 저장된 식당 정보 표시
                            MealWidgetError(
                                restaurant = restaurant?.korean ?: "",
                                mealTime = "점심",
                                text = "로딩 중",
                                glanceId = id,
                            )
                        }

                        is WidgetMealInfo.Unavailable -> {
                            MealWidgetError(
                                restaurant = restaurant?.korean ?: "",
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
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreview() {
        MealWidgetContent("저녁", listOf(listOf("밥", "국", "반찬", "음료")), Restaurant.DODAM.korean)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreviewError() {
        MealWidgetError("저녁", Restaurant.DODAM.korean, "에러임")
    }
}
