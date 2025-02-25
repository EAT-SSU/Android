package com.eatssu.android.presentation.widget.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eatssu.android.R
import com.eatssu.android.data.datastore.MealDataStore
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenusInformationList
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.enums.Restaurant
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MyAppWidget : GlanceAppWidget(

) {

    var currentIndex = 0 // 식당을 순차적으로 보여줄 인덱스

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val weatherDataStore = MealDataStore(context)
        val mealFlow = weatherDataStore.getMealFlow()

        scheduleWeatherUpdate(context)

        // 기본 스레드에서 실행
        // withContext를 사용해서 다른 스레드로 전환할 수 있음
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.


        provideContent {
            val meal by mealFlow.collectAsState(initial = "")
            // create your AppWidget here

            Timber.d(meal.toString())
            var isError = false;
            var data = null
            try {
//                getTodayMealUseCase
//                val repository = (context.applicationContext as MyApplication).myRepository
//                data = repository.loadData()
            } catch (e: Exception) {
                isError = true;
                //handleError
            }

            if (isError) {
                ErrorView()
            } else {
                MyContent()

            }
        }
    }


    @Composable
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 320, heightDp = 240)
    private fun MyContent() {
        val restaurants = getVariableRestaurants()
        val currentRestaurant = restaurants[currentIndex]

        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background)
//            .background(color = Color(0xFFD3D3D3)) //0xFFFAFAFB
                .padding(16.dp)
                .cornerRadius(20.dp),
        ) {

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
//            verticalAlignment = Alignment.Top,
//            horizontalAlignment = Alignment.Start
            ) {
                // 조식/중식/석식
                Column(
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        modifier = GlanceModifier.size(height = 14.dp, width = 43.dp),
                        provider = ImageProvider(R.drawable.img_new_logo_primary),
                        contentDescription = "Logo",
                    )

                    Text(
                        "중식", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(top = 2.dp, bottom = 12.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                //식당 이름
                var restaurantName = currentRestaurant.displayName

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally  // 수평 중앙 정렬
                ) {
                    Image(
                        modifier = GlanceModifier.size(18.dp),
                        provider = ImageProvider(R.drawable.ic_arrow_left),
                        contentDescription = "left",
                    )
                    Text(
                        restaurantName,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                    )
                    Image(
                        modifier = GlanceModifier.size(18.dp)
                            .clickable { actionRunCallback<ChangeAction>() },
                        provider = ImageProvider(R.drawable.ic_arrow_right),
                        contentDescription = "right"
                    )
                }
            }
            MenuList()
        }
    }

    @Composable
    fun MenuList() {
        val mealResponses = listOf(
            GetMealResponse(
                mealId = 1,
                price = 5000,
                rating = 4.5,
                briefMenus = arrayListOf(
                    MenusInformationList(menuId = 1, name = "김치찌개"),
                    MenusInformationList(menuId = 2, name = "불고기")
                )
            ),
            GetMealResponse(
                mealId = 2,
                price = 6000,
                rating = 4.0,
                briefMenus = arrayListOf(
                    MenusInformationList(menuId = 3, name = "된장찌개"),
                    MenusInformationList(menuId = 4, name = "제육볶음")
                )
            )
        )

        Box(
            modifier = GlanceModifier.fillMaxSize()
                .background(color = Color(0xFFFAFAFB)) //0xFFFAFAFB
                .padding(top = 5.dp, start = 14.dp, end = 5.dp)
                .cornerRadius(10.dp)
        )
        {
            LazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
                mealResponses.forEachIndexed { index, mealResponse ->
                    item {
                        val menuNames =
                            mealResponse.briefMenus.joinToString(separator = "+") { it.name ?: "" }
//                        MenuItem(menuNames)
                        Text(
                            text = menuNames,
                            style = TextStyle(fontSize = 12.sp)
                        )

                        // 다른 리스트와 구분을 위한 Spacer (마지막 항목이 아니면 한 줄 띄움)
//                        if (index < mealResponses.size - 1) {
                        Spacer(modifier = GlanceModifier.size(20.dp))
//                        }
                    }
                }
            }
        }
    }


    @Composable
    fun MenuItem(menuNames: String) {
        Column(
            modifier = GlanceModifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = menuNames,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }


    @Composable
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 320, heightDp = 240)
    fun ErrorView() {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(16.dp)
                .cornerRadius(20.dp),
        ) {

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
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

                    Text(
                        "중식", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                        modifier = GlanceModifier.padding(top = 2.dp, bottom = 12.dp)
                    )
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
                modifier = GlanceModifier.fillMaxSize()
                    .background(color = Color(0xFFFAFAFB))
                    .cornerRadius(10.dp)
            )
            {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        modifier = GlanceModifier.size(30.dp)
                            .padding(bottom = 6.dp),
                        provider = ImageProvider(R.drawable.ic_alert_circle),
                        contentDescription = "alert"
                    )
                    Text(
                        "네트워크 연결 상태를 확인해주세요.",
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                    )
                }
            }
        }
    }

    fun getVariableRestaurants(): List<Restaurant> {
        return Restaurant.values().filter { it.menuType == MenuType.VARIABLE }
    }

    suspend fun changeRestaurant() {
        currentIndex =
            (currentIndex - 1 + getVariableRestaurants().size) % getVariableRestaurants().size // 이전 식당으로 변경
        provideContent { MyContent() } // 텍스트를 갱신하는 방식으로 다시 렌더링

    }

    fun scheduleWeatherUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WeatherWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

}

class ChangeAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        MyAppWidget().update(context, glanceId)
    }
}
