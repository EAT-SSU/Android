package com.eatssu.android.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 위젯 업데이트 워커
class TimeUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 앱 위젯 매니저 가져오기
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

            // 위젯 구성 요소 식별자 생성
            val thisWidget = ComponentName(applicationContext, TimeWidgetProvider::class.java)

            // 등록된 모든 위젯 ID 가져오기
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            // 모든 위젯 업데이트
            if (appWidgetIds.isNotEmpty()) {
                for (widgetId in appWidgetIds) {
                    TimeWidgetProvider.updateAppWidget(
                        applicationContext,
                        appWidgetManager,
                        widgetId
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("TimeUpdateWorker", "위젯 업데이트 실패", e)
            Result.retry()
        }
    }
}
