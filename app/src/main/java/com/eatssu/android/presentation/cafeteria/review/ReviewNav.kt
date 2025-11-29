package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListScreen
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewScreen
import com.eatssu.android.presentation.cafeteria.review.write.WriteReviewScreen
import com.eatssu.common.enums.MenuType

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
        // 리뷰 보기
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
                        set("menuList", review.menuLikeInfoList)
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
            WriteReviewScreen(
                menuType = menuType,
                menuName = menuName,
                id = id,
                onBack = { navHostController.popBackStack() },
            )
        }

        // 리뷰 수정
        composable(ReviewNav.Modify) { backStackEntry ->
            val prev = navHostController.previousBackStackEntry?.savedStateHandle
            val reviewId = prev?.get<Long>("reviewId") ?: 0L
            val initialRating = prev?.get<Int>("initialRating") ?: 0
            val initialContent = prev?.get<String>("initialContent") ?: ""
            val menuLikeInfoNames =
                prev?.get<ArrayList<Review.MenuLikeInfo>>("menuList") ?: arrayListOf()

            //todo 밀이 아니라 메뉴일때 처리해야함
            // java.lang.ClassCastException: java.util.Collections$SingletonList cannot be cast to java.util.ArrayList

            ModifyReviewScreen(
                reviewId = reviewId,
                initialRating = initialRating,
                initialContent = initialContent,
                menuLikeInfoList = menuLikeInfoNames,
                onBack = { navHostController.popBackStack() },
            )
        }
    }
}