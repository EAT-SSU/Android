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
    menuName: String,
    menuType: MenuType,
    id: Long,
    onExit: () -> Unit = {}
) {

    NavHost(
        navController = nav,
        startDestination = ReviewNav.List
    ) {
        // 리뷰 리스트
        composable(ReviewNav.List) {
            ReviewListScreen(
                menuType = menuType,
                id = id,
                onReviewWriteButtonClick = { menuName ->
                    // SavedStateHandle을 사용하여 menuName 전달
                    nav.currentBackStackEntry?.savedStateHandle?.set("menuName", menuName)
                    
                    nav.navigate(ReviewNav.Write) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 리뷰 작성
        composable(ReviewNav.Write) { backStackEntry ->

        ReviewWriteScreen(
                menuType = menuType,
                menuName = menuName,
                id = id
            )
        }
    }
}