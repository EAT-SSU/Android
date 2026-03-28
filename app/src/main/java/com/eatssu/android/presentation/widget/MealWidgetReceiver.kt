package com.eatssu.android.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.eatssu.android.domain.usecase.widget.LoadRestaurantByFileKeyUseCase
import com.eatssu.android.presentation.widget.ui.MealWidget
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.WidgetAnalyticsEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var loadRestaurantByFileKeyUseCase: LoadRestaurantByFileKeyUseCase

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
                val widgetFileKey = "appWidget-$appWidgetId"
                val filename = "appWidgetLayout-${appWidgetId}"
                val dataStoreFile = File(context.filesDir, "datastore/$filename")
                val restaurant = loadRestaurantByFileKeyUseCase(widgetFileKey)

                if (dataStoreFile.exists()) {
                    dataStoreFile.delete()
                    Timber.d("Deleted DataStore file for widget $appWidgetId")
                }

                analyticsTracker.track(WidgetAnalyticsEvent.Removed(restaurant))

            }
        } catch (e: Exception) {
            Timber.e("Failed to cleanup DataStore for widget $appWidgetId: ${e.message}")
        }
    }
}
