package com.eatssu.android.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.eatssu.android.presentation.widget.ui.MealWidget
import com.eatssu.common.EventLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MealWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        // 삭제된 위젯들의 DataStore 파일 정리
        appWidgetIds.forEach { appWidgetId ->
            cleanupWidgetDataStore(context, appWidgetId)
        }
    }

    private fun cleanupWidgetDataStore(context: Context, appWidgetId: Int) {
        try {
            runBlocking {
                val filename = "appWidgetLayout-${appWidgetId}"
                val dataStoreFile = File(context.filesDir, "datastore/$filename")

                if (dataStoreFile.exists()) {
                    dataStoreFile.delete()
                    Timber.d("Deleted DataStore file for widget $appWidgetId")
                }

                EventLogger.removeWidget()

            }
        } catch (e: Exception) {
            Timber.e("Failed to cleanup DataStore for widget $appWidgetId: ${e.message}")
        }
    }
}