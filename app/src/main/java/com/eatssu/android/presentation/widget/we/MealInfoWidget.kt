package com.eatssu.android.presentation.widget.we

//import com.eatssu.android.presentation.widget.theme.EATSSUWidgetColorScheme
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
import com.eatssu.android.presentation.widget.we.theme.EATSSUWidgetColorScheme
import com.eatssu.android.presentation.widget.we.util.launchApp
import timber.log.Timber


class MealWidget : GlanceAppWidget() {
    override val stateDefinition = MealInfoStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            LaunchedEffect(key1 = Unit) {
                MealWorker.enqueue(context)
            }

            GlanceTheme(colors = EATSSUWidgetColorScheme.colors) {
                when (val state = currentState<MealInfo>()) {
                    is MealInfo.Available -> {
                        if (state.mealList.isNotEmpty()) {
                            MealWidgetContent(
                                mealTime = state.mealTime,
                                mealList = state.mealList
                            )
                        } else {
                            MealWidgetError(
                                mealTime = state.mealTime,
                                text = "오늘의 메뉴가 없습니다."
                            )

                        }
                    }

                    is MealInfo.Loading -> {
                        MealWidgetError(text = "로딩 중")
                    }

                    is MealInfo.Unavailable -> {
                        MealWidgetError(text = "네트워크 연결 상태를 확인해주세요.")
                    }
                }
            }
        }
    }

    @Composable
    private fun MealWidgetContent(
        mealTime: String,
        mealList: List<String>,
    ) {
        val context = LocalContext.current

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
                // 조식/중식/석식

                Image(
                    modifier = GlanceModifier.size(height = 14.dp, width = 43.dp),
                    provider = ImageProvider(R.drawable.img_new_logo_primary),
                    contentDescription = "Logo",
                )
                Spacer(modifier = GlanceModifier.size(8.dp))


                Text(
                    mealTime, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                )


                Spacer(modifier = GlanceModifier.defaultWeight())

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally  // 수평 중앙 정렬
                ) {
//                    Image(
//                        modifier = GlanceModifier.size(18.dp)
//                            .clickable {
//                                onLeftArrowClick()
//                                Timber.d("onLeftArrowClick")
//                            },
//                        provider = ImageProvider(R.drawable.ic_arrow_left),
//                        contentDescription = "left",
//                    )
                    Text(
                        "도담식당",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                    )
//                    Image(
//                        modifier = GlanceModifier.size(18.dp)
//                            .clickable {
//                                Timber.d("onRightArrowClick")
////                                actionRunCallback<NextRestaurantAction>()
////                                Timber.d("onRightArrowClick2")
//                            },
//                        provider = ImageProvider(R.drawable.ic_arrow_right),
//                        contentDescription = "right"
//                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .cornerRadius(10.dp)
                    .background(GlanceTheme.colors.onBackground),
            ) {
                itemsIndexed(mealList) { index, item ->
                    val text = if (item.length >= 14) item.substring(0, 13) + "..." else item

                    Column {
                        Text(
                            text = text,
//                        color = GlanceTheme.colors.tertiary.getColor(context),
//                        fontSize = 14.sp
                        )
                        if (mealList.lastIndex != index) {
                            Spacer(modifier = GlanceModifier.height(1.37.dp))
                        }
                    }
                }
            }
        }
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable {
                    context.launchApp()
                    Timber.d("위젯 클릭")
                },
            content = {}
        )
    }

    @Composable
    fun MealWidgetError(mealTime: String? = "", text: String) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(16.dp)
                .cornerRadius(20.dp)
        ) {

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 조식/중식/석식
                Column(
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        modifier = GlanceModifier.size(height = 14.dp, width = 43.dp),
                        provider = ImageProvider(R.drawable.img_new_logo_primary),
                        contentDescription = "Logo"
                    )

                    if (mealTime != null) {
                        Text(
                            mealTime,
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                            modifier = GlanceModifier.padding(top = 2.dp, bottom = 12.dp)
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                //식당 이름
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally  // 수평 중앙 정렬
                ) {
                    Image(
                        modifier = GlanceModifier.size(18.dp),
                        provider = ImageProvider(R.drawable.ic_arrow_left),
                        contentDescription = "left"
                    )
                    Text(
                        "기숙사 식당",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                    )
                    Image(
                        modifier = GlanceModifier.size(18.dp),
                        provider = ImageProvider(R.drawable.ic_arrow_right),
                        contentDescription = "right"
                    )
                }

            }

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .cornerRadius(10.dp)
                    .background(GlanceTheme.colors.onBackground)
            )
            {
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

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreview() {
        MealWidgetContent("저녁", listOf("밥", "국", "반찬", "음료"))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MealWidgetPreviewError() {
        MealWidgetError("저녁", "에러임")
    }

}

class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MealWidget()
}

