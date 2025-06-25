package com.eatssu.android.presentation.widget

//import com.eatssu.android.presentation.widget.medium.TodayMealWidget

//@HiltWorker
//class TodayMealWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted workerParams: WorkerParameters,
//    private val getTodayMealUseCase: GetTodayMealUseCase,
//    private val mealDataStore: MealDataStore
//) : CoroutineWorker(context, workerParams) {
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override suspend fun doWork(): Result {
//        Timber.d("워커 is working!")
//
//        return try {
//
//            val currentDate = LocalDate.now()
//            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//
//            val restaurant = mealDataStore.getSelectedRestaurant().first().name
//
//            //todo 유틸 함수로 바꿀 것
//            val time = LocalTime.now()
//            val timeString: String = when (time.hour) {
//                in 0..9 -> Time.MORNING.name //아침
//                in 10..15 -> Time.LUNCH.name //점심
//                in 16..24 -> Time.DINNER.name //저녁
//                else -> ""
//            }
//
//            Timber.d("Date: $formattedDate, Restaurant: $restaurant, Time: $timeString")
//
//            // Flow 처리: fetchWeatherData()의 결과를 collect하여 처리
//            getTodayMealUseCase.fetchMealData(formattedDate, restaurant, timeString)
//                .collect { apiResult ->
//                    Timber.d("Meal data: $apiResult")
//
//                    GlanceAppWidgetManager(applicationContext).getGlanceIds(TodayMealWidget::class.java)
//                        .forEach { glanceId ->
//                            TodayMealWidget().update(applicationContext, glanceId)
//                        }
//                }
//
//            Result.success()
//        } catch (e: Exception) {
//            Timber.e(e.toString())
//            Result.retry()
//        }
//
//    }
//}
