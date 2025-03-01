package com.eatssu.android.presentation.widget.compose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eatssu.android.domain.usecase.meal.GetTodayMealUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber


@HiltWorker
class MealWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getTodayMealUseCase: GetTodayMealUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // 여기서 단순히 Log 찍기나 간단한 작업만 하면 됨
        Timber.d("워커 SimpleWorker is working!")
        return Result.success()
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override suspend fun doWork(): Result {
//        Timber.d("워커 시작")
//        Log.d("\"워커 시작\"", "워커 시작")
//
//        return try {
//
//            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
//
//            val date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//            val restaurant = Restaurant.DODAM.name //todo 교체
//
//            val time = LocalTime.now()
//            val timeString: String = when (time.hour) {
//                in 0..9 -> Time.MORNING.name //아침
//                in 10..15 -> Time.LUNCH.name //점심
//                in 16..24 -> Time.DINNER.name //저녁
//                else -> ""
//            }
//
//            Timber.d("Date: $date, Restaurant: $restaurant, Time: $timeString")
//            val data = getTodayMealUseCase.execute(date, restaurant, Time.LUNCH.name)
//
//            Timber.d(data.toString())
//
//            Log.d("data", data.toString())
//            // 위젯 업데이트
//            GlanceAppWidgetManager(applicationContext).getGlanceIds(MyAppWidget::class.java)
//                .forEach { glanceId ->
//                    MyAppWidget().update(applicationContext, glanceId)
//                }
//
//            Result.success()
//        } catch (e: Exception) {
//            Timber.e(e.toString())
//            Result.retry()
//        }
//    }

}
