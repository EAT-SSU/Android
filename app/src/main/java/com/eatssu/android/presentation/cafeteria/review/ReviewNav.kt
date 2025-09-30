package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListScreen
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewScreen
import com.eatssu.android.presentation.cafeteria.review.write.ReviewWriteScreen

object ReviewNav {
    const val List = "list"
    const val Write = "write"
    const val Modify = "modify"
}

@Composable
fun ReviewNav(
    navHostController: NavHostController = rememberNavController(),
    menuName: String,
    menuType: MenuType,
    id: Long,
    onExit: () -> Unit = {}
) {

    NavHost(
        navController = navHostController,
        startDestination = ReviewNav.List
    ) {
        // 리뷰 리스트
        composable(ReviewNav.List) {
            ReviewListScreen(
                menuName = menuName,
                menuType = menuType,
                id = id,
                onBack = { onExit() },
                onModifyClick = { review ->
                    // 선택된 리뷰 데이터를 Modify 화면으로 전달
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("reviewId", review.reviewId)
                        set("initialRating", review.rating)
                        set("initialContent", review.content)
                        set("menuList", review.menuList)
                    }

                    navHostController.navigate(ReviewNav.Modify) { launchSingleTop = true }
                },
                onWriteButtonClick = {
                    navHostController.navigate(ReviewNav.Write) {
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
                id = id,
                onBack = { navHostController.popBackStack() },
            )
        }

        // 리뷰 작성
        composable(ReviewNav.Modify) { backStackEntry ->
            val prev = navHostController.previousBackStackEntry?.savedStateHandle
            val reviewId = prev?.get<Long>("reviewId") ?: 0L
            val initialRating = prev?.get<Int>("initialRating") ?: 0
            val initialContent = prev?.get<String>("initialContent") ?: ""
            val menuNames = prev?.get<ArrayList<Review.Menu>>("menuList") ?: arrayListOf()

            ModifyReviewScreen(
                reviewId = reviewId,
                initialRating = initialRating,
                initialContent = initialContent,
                menuList = menuNames,
                onBack = { navHostController.popBackStack() },
                navController = navHostController
            )
        }
    }
}