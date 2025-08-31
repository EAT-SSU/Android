package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListScreen
import com.eatssu.android.presentation.cafeteria.review.write.ReviewWriteScreen

object ReviewNav {
    const val List = "list"
    const val Write = "write"
    const val Modify = "modify"
}

@Composable
fun ReviewNav(
    nav: NavHostController = rememberNavController(),
    menuType: MenuType,
    id: Long,
    onExit: () -> Unit = {}
) {

    NavHost(
        navController = nav,
        startDestination = ReviewNav.List
    ) {
        // 최소형(POI 내부 시작점, 실제로는 바로 Search로 밀어버림)
        composable(ReviewNav.List) {
            ReviewListScreen(
                menuType = menuType,
                id = id,
                onReviewWriteButtonClick = {
                    nav.navigate(ReviewNav.Write) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 자동완성
        composable(ReviewNav.Write) {
            ReviewWriteScreen(
//                onSearchBarClick = {
//                    nav.navigate(PoiRoutes.Suggest) {
//                        launchSingleTop = true
//                    }
//                },
//                onQueryClick = { query ->
//                    nav.navigate(PoiRoutes.resultRoute(query)) {
//                        launchSingleTop = true
//                    }
//                },
//                onPlaceClick = { placeName, x, y ->
//                    nav.navigate(PoiRoutes.routeSearch(placeName, x, y)) {
//                        launchSingleTop = true
//                    }
//                }
            )
        }
    }
}