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
import com.eatssu.android.domain.usecase.widget.GetTodayMealUseCase
import com.eatssu.android.domain.usecase.widget.LoadRestaurantByFileKeyUseCase
import com.eatssu.android.presentation.widget.ui.MealWidget
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
    private var loadRestaurantByFileKeyUseCase: LoadRestaurantByFileKeyUseCase,
) : CoroutineWorker(context, workParams) {
    companion object {
        private val uniqueWorkName = MealWorker::class.java.simpleName

        @RequiresApi(Build.VERSION_CODES.O)
        fun enqueue(context: Context) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MealWorker>(
                Duration.ofMinutes(60)
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
            // glanceId를 사용하여 정확한 식당 정보 가져오기
            val restaurant = loadRestaurantByFileKeyUseCase("appWidget-${appWidgetId}")
            Timber.d("MealWorker: glanceId=$glanceId, appWidgetId=$appWidgetId, restaurant=$restaurant")
            if (restaurant != null) {
                // 저장된 식당 정보가 있으면 3개 식사 시간의 메뉴를 모두 가져와서 위젯 상태 업데이트
                val currentMealTime = WidgetDataDisplayManager.getCurrentMealTime()
                val newState = WidgetDataDisplayManager.fetchMealInfo(
                    getMealsUseCase = getMealsUseCase,
                    requestedMealTime = currentMealTime,
                    restaurant = restaurant
                )

                setWidgetState(glanceId = glanceId, newState = newState)
                Timber.d("MealWorker: 위젯 상태 업데이트 완료 - 식당: ${restaurant.name}, 시간: $currentMealTime")
            } else {
                Timber.w("No restaurant saved for glanceId: $glanceId, skipping widget update")
            }
        }

        // 캐시 상태 로그 출력
        WidgetCacheManager.logCacheStatus()
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
}