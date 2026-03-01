package com.eatssu.android.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.eatssu.android.presentation.widget.ui.MealWidget
import com.eatssu.common.EventLogger
import dagger.hilt.android.AndroidEntryPoint
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
            val fileKey = "appWidget-$appWidgetId"
            val dataStoreFile = MealInfoStateDefinition.getLocation(context, fileKey)
            val tempDataStoreFile = File("${dataStoreFile.absolutePath}.tmp")

            if (dataStoreFile.exists() && dataStoreFile.delete()) {
                Timber.d("Deleted widget DataStore file for widget %d", appWidgetId)
            }

            if (tempDataStoreFile.exists() && tempDataStoreFile.delete()) {
                Timber.d("Deleted widget DataStore temp file for widget %d", appWidgetId)
            }

            EventLogger.removeWidget()
        } catch (error: Exception) {
            Timber.e(error, "Failed to cleanup widget DataStore for widget %d", appWidgetId)
        }
    }
}
