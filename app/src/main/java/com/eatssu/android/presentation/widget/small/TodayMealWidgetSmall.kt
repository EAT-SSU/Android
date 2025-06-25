package com.eatssu.android.presentation.widget.small

//import com.eatssu.android.presentation.widget.TodayMealWorker


//class TodayMealWidgetSmall : GlanceAppWidget() {
//
////    var currentIndex = 0 // 식당을 순차적으로 보여줄 인덱스
//
//    override suspend fun provideGlance(context: Context, id: GlanceId) {
//        val weatherDataStore = MealDataStore(context)
//        val mealFlow = weatherDataStore.mealFlow
//
//        scheduleWeatherUpdate(context)
//
//        // 기본 스레드에서 실행
//        // withContext를 사용해서 다른 스레드로 전환할 수 있음
//
//
//        provideContent { //이 함수가 렌더링 하는 함수임
//
//            val meal by mealFlow.collectAsState(
//                initial = WidgetMeal(
//                    "",
//                    "",
//                    "",
//                    ArrayList()
//                )
//            ) // 초기값을 빈 ArrayList로 설정
//
//
//            if (meal.menuList.isEmpty()) { //todo 네트워크 오류 분기처리
//                WidgetBaseLayout(
//                    restaurantName = meal.restaurantName,
//                    time = meal.time,
//                    content = { EmptyMenuContent(isNetworkError = false) }
//                )
//            } else {
//                WidgetBaseLayout(
//                    restaurantName = meal.restaurantName,
//                    time = meal.time,
//                    content = { MenuList(meal.menuList) }
//                )
//            }
//        }
//    }
//
//
//    @Composable
//    private fun WidgetBaseLayout(
//        restaurantName: String,
//        time: String,
//        onLeftArrowClick: () -> Unit = {},
////        onRightArrowClick: () -> Unit = { actionRunCallback<ChangeAction>() },
//        content: @Composable () -> Unit
//    ) {
//        val restaurants = getVariableRestaurants()
////        val currentRestaurant = restaurants[currentIndex]
//
//        Column(
//            modifier = GlanceModifier.fillMaxSize()
//                .background(GlanceTheme.colors.background)
//                .padding(16.dp)
//                .cornerRadius(20.dp)
//                .clickable {
//                    actionStartActivity<MainActivity>()
//                },
//        ) {
//
//            Row(
//                modifier = GlanceModifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally  // 수평 중앙 정렬
//            ) {
//                Image(
//                    modifier = GlanceModifier.size(18.dp),
//                    provider = ImageProvider(R.drawable.ic_arrow_left),
//                    contentDescription = "left",
//                )
//                Text(
//                    restaurantName,
//                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
//                    modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
//                )
//                Image(
//                    modifier = GlanceModifier.size(18.dp),
////                        .clickable { actionRunCallback<ChangeAction>() },
//                    provider = ImageProvider(R.drawable.ic_arrow_right),
//                    contentDescription = "right"
//                )
//
//                Text(
//                    time,
//                    modifier = GlanceModifier.padding(start = 4.dp),
//                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
//                )
//
//                Spacer(modifier = GlanceModifier.defaultWeight())
//
//                Image(
//                    modifier = GlanceModifier.size(10.dp),
//                    provider = ImageProvider(R.drawable.ic_mini_logo_mint),
//                    contentDescription = "Logo",
//                )
//            }
//
//
//            // 콘텐츠 영역
//            content()
//        }
//    }
//
//    @Composable
//    fun MenuList(mealList: List<String>) {
//        Box(
//            modifier = GlanceModifier.fillMaxSize()
//                .background(color = Color(0xFFFAFAFB))
//                .padding(top = 7.dp, start = 10.dp, end = 7.dp)
//                .cornerRadius(10.dp)
//        )
//        {
//            Column(
//                modifier = GlanceModifier.fillMaxSize(),
//                verticalAlignment = Alignment.CenterVertically // 세로 가운데 정렬
//            ) {
//                LazyColumn(
//                    modifier = GlanceModifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.Start
//                ) {
//
//                    mealList.forEachIndexed { index, menuString ->
//                        item {
//                            Text(
//                                modifier = GlanceModifier,
//                                text = menuString,
//                                style = TextStyle(fontSize = 10.sp),
//                            )
//                        }
//                        // 마지막 아이템이 아니면 Spacer 추가
//                        if (index < mealList.size - 1) {
//                            item {
//                                Spacer(modifier = GlanceModifier.size(12.dp))
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//    }
//
//
//    @Composable
//    fun EmptyMenuContent(isNetworkError: Boolean = false) {
//        Box(
//            modifier = GlanceModifier.fillMaxSize()
//                .background(color = Color(0xFFFAFAFB))
//                .cornerRadius(10.dp)
//        )
//        {
//            Column(
//                modifier = GlanceModifier
//                    .fillMaxSize(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Image(
//                    modifier = GlanceModifier.size(30.dp)
//                        .padding(bottom = 6.dp),
//                    provider = ImageProvider(R.drawable.ic_alert_circle),
//                    contentDescription = "alert"
//                )
//                Text(
//                    text = if (isNetworkError) {
//                        "네트워크 연결 상태를 확인해주세요."
//                    } else {
//                        "오늘의 메뉴가 없습니다."
//                    },
//                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
//                )
//            }
//        }
//    }
//
//    @Composable
//    @OptIn(ExperimentalGlancePreviewApi::class)
//    @Preview(widthDp = 200, heightDp = 200)
//    fun PreviewWidget1() {
//        val mealResponses = listOf(
//            "김치찌개+불고기",
//            "된장찌개+제육볶음"
//        )
//
//        WidgetBaseLayout(
//            restaurantName = "학생 식당",
//            time = "석식",
//            content = { MenuList(mealResponses) }
//        )
//    }
//
//    @Composable
//    @OptIn(ExperimentalGlancePreviewApi::class)
//    @Preview(widthDp = 200, heightDp = 200)
//    fun PreviewWidget2() {
//
//        WidgetBaseLayout(
//            restaurantName = "기숙사 식당",
//            time = "조식",
//            content = { EmptyMenuContent(isNetworkError = false) }
//        )
//    }
//
//    @Composable
//    @OptIn(ExperimentalGlancePreviewApi::class)
//    @Preview(widthDp = 200, heightDp = 200)
//    fun PreviewWidget3() {
//
//        WidgetBaseLayout(
//            restaurantName = "도담 식당",
//            time = "중식",
//            content = { EmptyMenuContent(isNetworkError = true) }
//        )
//    }
//
//
//    fun getVariableRestaurants(): List<Restaurant> {
//        return Restaurant.values().filter { it.menuType == MenuType.VARIABLE }
//    }
//
////    suspend fun changeRestaurant() {
////        currentIndex =
////            (currentIndex - 1 + getVariableRestaurants().size) % getVariableRestaurants().size // 이전 식당으로 변경
////        provideContent { MyContent() } // 텍스트를 갱신하는 방식으로 다시 렌더링
////
////    }
//
//    fun scheduleWeatherUpdate(context: Context) {
//        Log.d("워커", "워커가 등록됩니다")
//        val workRequest = PeriodicWorkRequestBuilder<TodayMealWorker>(60, TimeUnit.MINUTES)
//            .setConstraints(
//                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//            )
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "TodayMealWorker",
//            ExistingPeriodicWorkPolicy.UPDATE,
//            workRequest
//        )
//    }
//}