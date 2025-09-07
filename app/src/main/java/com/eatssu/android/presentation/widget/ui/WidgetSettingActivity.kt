package com.eatssu.android.presentation.widget.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.domain.usecase.widget.SaveRestaurantByFileKeyUseCase
import com.eatssu.android.presentation.widget.MealWorker
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WidgetSettingActivity : ComponentActivity() {

    @Inject
    lateinit var saveRestaurantByFileKeyUseCase: SaveRestaurantByFileKeyUseCase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {

                val restaurantOptions = Restaurant.getVariableRestaurantList().map {
                    it.displayName
                } // 변동 식당만 불러옵니다. 하드코딩 x

                var selectedRestaurant by rememberSaveable { mutableStateOf(restaurantOptions[0]) }

                val appWidgetId = intent?.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                var glanceId by remember { mutableStateOf<GlanceId?>(null) }
                val context = LocalContext.current
                LaunchedEffect(appWidgetId) {
                    glanceId =
                        if (appWidgetId != null && appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                            GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                    } else {
                        null
                    }
                }

                WidgetSettingScreen(
                    restaurantOptionList = restaurantOptions,
                    selectedRestaurant = selectedRestaurant,
                    onSelectRestaurant = { displayName ->
                        selectedRestaurant = displayName
                    },
                    onConfirm = { selectedRestaurantValue ->
                        if (glanceId == null) {
                            finish()
                            return@WidgetSettingScreen
                        }

                        lifecycleScope.launch {

                            saveRestaurantByFileKeyUseCase(
                                "appWidget-${appWidgetId}",
                                selectedRestaurantValue
                            )

                            // 위젯 업데이트
                            glanceId?.let {
                                MealWidget().update(this@WidgetSettingActivity, it)
                            }

                            // MealWorker 실행
                            MealWorker.enqueue(this@WidgetSettingActivity)
                            
                            Timber.d("선택하기 버튼으로 저장: $selectedRestaurantValue for glanceId: $glanceId")
                        }

                        // 결과 설정
                        val resultIntent = Intent().apply {
                            putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                appWidgetId ?: AppWidgetManager.INVALID_APPWIDGET_ID
                            )
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
