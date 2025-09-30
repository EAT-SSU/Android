package com.eatssu.android.presentation.mypage.myreview

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewScreen

object MyReviewNav {
    const val List = "list"
    const val Modify = "modify"
}

@Composable
fun MyReviewNav(
    navHostController: NavHostController = rememberNavController(),
    onExit: () -> Unit = {}
) {

    NavHost(
        navController = navHostController,
        startDestination = MyReviewNav.List
    ) {
        // 리뷰 리스트
        composable(MyReviewNav.List) {
            MyReviewListScreen(
                onBack = { onExit() },
                onModifyClick = { review ->
                    // 선택된 리뷰 데이터를 Modify 화면으로 전달
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("reviewId", review.reviewId)
                        set("initialRating", review.mainGrade)
                        set("initialContent", review.content)
                        set("menuList", review.menuList)
                    }

                    navHostController.navigate(MyReviewNav.Modify) { launchSingleTop = true }
                },
            )
        }

        // 리뷰 작성
        composable(MyReviewNav.Modify) { backStackEntry ->
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