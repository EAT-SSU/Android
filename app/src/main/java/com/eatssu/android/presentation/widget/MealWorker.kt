package com.eatssu.android.presentation.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.domain.usecase.meal.GetTodayMealUseCase
import com.eatssu.android.presentation.widget.ui.WidgetSettingActivity
import com.eatssu.android.presentation.widget.util.MealInfoState
import com.eatssu.android.presentation.widget.util.WidgetDataDisplayManager
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
        glanceIds.forEach { glanceId ->
            val appWidgetId = manager.getAppWidgetId(glanceId)
            val restaurant = WidgetSettingActivity.loadRestaurantPref(context, appWidgetId)
            setWidgetState(
                glanceId = glanceId,
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

    private suspend fun setWidgetState(glanceId: GlanceId, newState: WidgetMealInfo) {
        updateAppWidgetState(
            context = context,
            definition = MealInfoStateDefinition,
            glanceId = glanceId,
            updateState = { newState }
        )
        MealWidget().update(context, glanceId)
    }

    private fun MealInfoState.toMealInfo(): WidgetMealInfo = when (this) {
        is MealInfoState.Available -> WidgetMealInfo.Available(mealTime, mealList, restaurant)
        is MealInfoState.Unavailable -> WidgetMealInfo.Unavailable
        is MealInfoState.Loading -> WidgetMealInfo.Loading
    }
}