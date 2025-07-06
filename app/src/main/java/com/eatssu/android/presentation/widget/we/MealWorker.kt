package com.eatssu.android.presentation.widget.we

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eatssu.android.domain.usecase.meal.GetTodayMealUseCase
import com.eatssu.android.presentation.widget.we.util.MealInfoState
import com.eatssu.android.presentation.widget.we.util.WidgetDataDisplayManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.time.Duration

@HiltWorker
class MealWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private var getMealsUseCase: GetTodayMealUseCase,
) : CoroutineWorker(context, workParams) {
    companion object {
        private val uniqueWorkName = MealWorker::class.java.simpleName

        @RequiresApi(Build.VERSION_CODES.O)
        fun enqueue(context: Context) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MealWorker>(
                Duration.ofMinutes(30)
            )

            Timber.d("Widget - enqueue")
            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                requestBuilder.build()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MealWidget::class.java)
        // 여러 위젯 지원: 각 위젯별로 식당 설정을 불러와서 fetchMealInfo에 전달
        glanceIds.forEach { glanceId ->
            // appWidgetId 추출
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetComponent = ComponentName(context, MealWidgetReceiver::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
            val appWidgetId = appWidgetIds.firstOrNull() ?: 0
            val restaurant = MealWidgetConfigureActivity.loadRestaurantPref(context, appWidgetId)
            setWidgetState(
                newState = WidgetDataDisplayManager.fetchMealInfo(
                    getMealsUseCase = getMealsUseCase,
                    requestedMealTime = WidgetDataDisplayManager.getCurrentMealTime(),
                    restaurant = restaurant
                ).toMealInfo()
            )
        }
        Timber.d("Widget - 워커는 doWork")
        return Result.success()
    }

    private suspend fun setWidgetState(newState: MealInfo) {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MealWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = MealInfoStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        MealWidget().updateAll(context)
    }

    private fun MealInfoState.toMealInfo(): MealInfo = when (this) {
        is MealInfoState.Available -> MealInfo.Available(mealTime, mealList, restaurant)
        is MealInfoState.Unavailable -> MealInfo.Unavailable
        is MealInfoState.Loading -> MealInfo.Loading
    }
}