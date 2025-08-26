package com.eatssu.android.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.eatssu.android.presentation.widget.ui.WidgetSettingActivity
import com.eatssu.android.presentation.widget.util.WidgetCacheManager
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File

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
                val restaurant = WidgetSettingActivity.loadRestaurantPref(context, appWidgetId)
                val filename = "MealInfo_${restaurant.name}"
                val dataStoreFile = File(context.filesDir, "datastore/$filename.preferences_pb")

                if (dataStoreFile.exists()) {
                    dataStoreFile.delete()
                    Timber.d("Deleted DataStore file for widget $appWidgetId (${restaurant.name})")
                }

                // 캐시도 정리
                WidgetCacheManager.clearCacheForRestaurant(restaurant)
            }
        } catch (e: Exception) {
            Timber.e("Failed to cleanup DataStore for widget $appWidgetId: ${e.message}")
        }
    }
} 