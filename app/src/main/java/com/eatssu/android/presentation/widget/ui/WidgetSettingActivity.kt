package com.eatssu.android.presentation.widget.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.android.domain.usecase.widget.LoadRestaurantByFileKeyUseCase
import com.eatssu.android.domain.usecase.widget.SaveRestaurantByFileKeyUseCase
import com.eatssu.android.presentation.widget.MealWorker
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.WidgetAnalyticsEvent
import com.eatssu.common.enums.Restaurant
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WidgetSettingActivity : ComponentActivity() {

    @Inject
    lateinit var saveRestaurantByFileKeyUseCase: SaveRestaurantByFileKeyUseCase

    @Inject
    lateinit var loadRestaurantByFileKeyUseCase: LoadRestaurantByFileKeyUseCase

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {

                    val restaurants = Restaurant.getVariableRestaurantList()
                    val restaurantOptions =
                        restaurants.map { getString(it.displayNameResId) } // 변동 식당만 불러옵니다. 하드코딩 x

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

                        val fileKey =
                            appWidgetId?.takeIf { it != AppWidgetManager.INVALID_APPWIDGET_ID }
                                ?.let { "appWidget-$it" }
                        val savedRestaurant = fileKey?.let { loadRestaurantByFileKeyUseCase(it) }
                        if (savedRestaurant != null) {
                            selectedRestaurant = getString(savedRestaurant.displayNameResId)
                        }
                    }

                    WidgetSettingScreen(
                        restaurantOptionList = restaurantOptions,
                        selectedRestaurant = selectedRestaurant,
                        onSelectRestaurant = { displayName ->
                            selectedRestaurant = displayName
                        },
                        onConfirm = { selectedRestaurantValue ->
                            val currentGlanceId = glanceId
                            if (currentGlanceId == null || appWidgetId == null) {
                                finish()
                                return@WidgetSettingScreen
                            }

                            lifecycleScope.launch {
                                val fileKey = "appWidget-$appWidgetId"
                                val previousRestaurant = loadRestaurantByFileKeyUseCase(fileKey)

                                saveRestaurantByFileKeyUseCase(
                                    fileKey,
                                    selectedRestaurantValue
                                )

                                trackWidgetSelection(previousRestaurant, selectedRestaurantValue)

                                // 위젯 업데이트
                                MealWidget().update(this@WidgetSettingActivity, currentGlanceId)

                                // MealWorker 실행
                                MealWorker.enqueue(this@WidgetSettingActivity)

                                Timber.d("선택하기 버튼으로 저장: $selectedRestaurantValue for glanceId: $currentGlanceId")

                                // 결과 설정
                                val resultIntent = Intent().apply {
                                    putExtra(
                                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                                        appWidgetId
                                    )
                                }
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            }
                        },
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    private fun trackWidgetSelection(
        previousRestaurant: Restaurant?,
        selectedRestaurant: Restaurant,
    ) {
        when {
            previousRestaurant == null -> {
                analyticsTracker.track(WidgetAnalyticsEvent.Added(selectedRestaurant))
            }

            previousRestaurant != selectedRestaurant -> {
                analyticsTracker.track(
                    WidgetAnalyticsEvent.Changed(
                        restaurantBefore = previousRestaurant,
                        restaurantAfter = selectedRestaurant,
                    ),
                )
            }
        }
    }
}
