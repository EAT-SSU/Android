package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.data.enums.MenuType
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
                        set("initialRating", review.mainGrade)
                        set("initialContent", review.content)
                        // 메뉴는 (id, name) 쌍이 필요하므로 이름만 전달하는 경우, id 매핑은 서버/화면에서 보유하고 있어야 합니다.
                        // 여기서는 임시로 name만 전달. Modify에서 Pair<Long,String>로 이미 있는 경우 그걸 넣어주세요.
                        set("menuList", ArrayList(review.menuList))
//                        set("likeMenuList", ArrayList(review.likeMenuList ?: emptyList()))
                    }

                    navHostController.navigate(ReviewNav.Modify) { launchSingleTop = true }
                },
                onWriteButtonClick = { menuName ->
                    // SavedStateHandle을 사용하여 menuName 전달
                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                        "menuName",
                        menuName
                    )

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
                navController = navHostController,
                onBack = { navHostController.popBackStack() },
            )
        }

        // 리뷰 작성
        composable(ReviewNav.Modify) { backStackEntry ->
            val prev = navHostController.previousBackStackEntry?.savedStateHandle
            val reviewId = prev?.get<Long>("reviewId") ?: 0L
            val initialRating = prev?.get<Int>("initialRating") ?: 0
            val initialContent = prev?.get<String>("initialContent") ?: ""
            val menuNames = prev?.get<ArrayList<String>>("menuList") ?: arrayListOf()
            val likeMenuList = prev?.get<ArrayList<String>>("likeMenuList") ?: arrayListOf()

            ModifyReviewScreen(
                reviewId = reviewId,
                initialRating = initialRating,
                initialContent = initialContent,
                menuList = menuNames.mapIndexed { index, name -> index.toLong() to name },
                likedNames = likeMenuList,
                onBack = { navHostController.popBackStack() },
                navController = navHostController
            )
        }
    }
}