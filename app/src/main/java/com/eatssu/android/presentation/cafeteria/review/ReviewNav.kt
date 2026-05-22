package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListScreen
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewScreen
import com.eatssu.android.presentation.cafeteria.review.write.WriteReviewScreen
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant

object ReviewNav {
    const val List = "list"
    const val Write = "write"
    const val Modify = "modify"
}

private const val KEY_REVIEW_LIST_REFRESH_NONCE = "review_list_refresh_nonce"

@Composable
fun ReviewNav(
    navHostController: NavHostController = rememberNavController(),
    menuName: String,
    menuType: MenuType,
    restaurant: Restaurant,
    id: Long,
    onExit: () -> Unit = {}
) {

    NavHost(
        navController = navHostController,
        startDestination = ReviewNav.List
    ) {
        // 리뷰 보기
        composable(ReviewNav.List) { backStackEntry ->
            val refreshNonce by backStackEntry.savedStateHandle
                .getStateFlow(KEY_REVIEW_LIST_REFRESH_NONCE, 0L)
                .collectAsState()

            ReviewListScreen(
                menuName = menuName,
                menuType = menuType,
                restaurant = restaurant,
                id = id,
                refreshNonce = refreshNonce,
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

            LaunchedEffect(refreshNonce) {
                if (refreshNonce != 0L) {
                    backStackEntry.savedStateHandle[KEY_REVIEW_LIST_REFRESH_NONCE] = 0L
                }
            }
        }

        // 리뷰 작성
        composable(ReviewNav.Write) { backStackEntry ->
            WriteReviewScreen(
                menuType = menuType,
                restaurant = restaurant,
                menuName = menuName,
                id = id,
                onBack = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(KEY_REVIEW_LIST_REFRESH_NONCE, System.currentTimeMillis())
                    navHostController.popBackStack()
                },
            )
        }

        // 리뷰 수정
        composable(ReviewNav.Modify) { backStackEntry ->
            val prev = navHostController.previousBackStackEntry?.savedStateHandle
            val reviewId = prev?.get<Long>("reviewId") ?: 0L
            val initialRating = prev?.get<Int>("initialRating") ?: 0
            val initialContent = prev?.get<String>("initialContent") ?: ""
            val menuLikeInfoNames = prev?.get<List<Review.MenuLikeInfo>>("menuList")
                ?.let { ArrayList(it) } ?: arrayListOf()

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
