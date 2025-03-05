package com.eatssu.android.presentation.widget

// 이전 식당으로 이동
//class PrevRestaurantAction : ActionCallback {
//    override suspend fun onAction(
//        context: Context,
//        glanceId: GlanceId,
//        parameters: ActionParameters
//    ) {
//        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, MealDataStoreEntryPoint::class.java)
//        val mealDataStore = hiltEntryPoint.mealDataStore()
//
//        mealDataStore.moveToPreviousRestaurant()
//        Timber.d("액션 속")
//        updateWidget(context)
//    }
//}
//
//
//class NextRestaurantAction : ActionCallback {
//    override suspend fun onAction(
//        context: Context,
//        glanceId: GlanceId,
//        parameters: ActionParameters
//    ) {
//
//        Log.d("NextRestaurantAction", "onAction called")
//
//        try {
////            val hiltEntryPoint =
////                EntryPointAccessors.fromApplication(context, MealDataStoreEntryPoint::class.java)
////            val mealDataStore = hiltEntryPoint.mealDataStore()
////            Timber.d("MealDataStore: $mealDataStore")
////
////
////            mealDataStore.moveToNextRestaurant()
////            Timber.d("액션 속")
//////        updateWidget(context)
////
////            // Force widget update
////            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
////            val currentGlanceIds = glanceAppWidgetManager.getGlanceIds(TodayMealWidget::class.java)
////
////            currentGlanceIds.forEach { currentGlanceId ->
////                Timber.d("Updating widget ID: $currentGlanceId")
////                TodayMealWidget().update(context, currentGlanceId)
////            }
////
////            // Additionally schedule the worker
////            updateWidget(context)
//        } catch (e: Exception) {
//            Timber.e("Error in NextRestaurantAction: ${e.message}")
//
//        }
//    }
//}
//    // 위젯 업데이트 요청
//    private fun updateWidget(context: Context) {
//        Timber.d("Updating widget...")  // 이 부분에서 로그가 찍히는지 확인해보세요.
//        val request = OneTimeWorkRequestBuilder<TodayMealWorker>().build()
//        WorkManager.getInstance(context).enqueue(request)
//    }
//
