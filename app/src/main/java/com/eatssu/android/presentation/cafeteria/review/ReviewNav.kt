package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.MenuList
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
                onReviewWriteButtonClick = { menuList ->
                    // SavedStateHandle을 사용하여 menuList 전달
                    nav.currentBackStackEntry?.savedStateHandle?.set("menuList", menuList)
                    
                    nav.navigate(ReviewNav.Write) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 자동완성
        composable(ReviewNav.Write) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val menuList = savedStateHandle.get<ArrayList<MenuList>>("menuList") ?: arrayListOf()
            
            ReviewWriteScreen(
                menuType = menuType,
                menuList = menuList,
                id = id
            )
        }
    }
}