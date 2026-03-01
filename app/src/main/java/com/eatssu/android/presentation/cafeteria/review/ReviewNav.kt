package com.eatssu.android.presentation.cafeteria.review

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListRoute
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewRoute
import com.eatssu.android.presentation.cafeteria.review.write.WriteReviewRoute
import com.eatssu.android.presentation.navigation.ReviewDestination
import com.eatssu.android.presentation.navigation.ReviewModifyPayloadCodec
import com.eatssu.common.enums.MenuType
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun ReviewNav(
    navHostController: NavHostController = rememberNavController(),
    menuName: String,
    menuType: MenuType,
    id: Long,
    onExit: () -> Unit = {},
    onNavigateToReport: (reviewId: Long) -> Unit = {},
) {
    val isPreviewMode = LocalInspectionMode.current

    NavHost(
        navController = navHostController,
        startDestination = ReviewDestination.List,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable<ReviewDestination.List> {
            if (isPreviewMode) {
                ReviewNavPreviewPlaceholder(title = "Review List")
            } else {
                ReviewListRoute(
                    menuName = menuName,
                    menuType = menuType,
                    id = id,
                    onBack = { onExit() },
                    onModifyClick = { review ->
                        navHostController.navigate(
                            ReviewDestination.Modify(
                                reviewId = review.reviewId,
                                initialRating = review.rating,
                                initialContent = review.content,
                                menuLikeInfoPayload = ReviewModifyPayloadCodec.encode(review.menuLikeInfoList),
                            )
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onWriteButtonClick = {
                        navHostController.navigate(ReviewDestination.Write) {
                            launchSingleTop = true
                        }
                    },
                    onReportClick = onNavigateToReport,
                )
            }
        }

        composable<ReviewDestination.Write> {
            if (isPreviewMode) {
                ReviewNavPreviewPlaceholder(title = "Write Review")
            } else {
                WriteReviewRoute(
                    menuType = menuType,
                    menuName = menuName,
                    id = id,
                    onBack = { navHostController.popBackStack() },
                )
            }
        }

        composable<ReviewDestination.Modify> { backStackEntry ->
            if (isPreviewMode) {
                ReviewNavPreviewPlaceholder(title = "Modify Review")
            } else {
                val route = backStackEntry.toRoute<ReviewDestination.Modify>()

                ModifyReviewRoute(
                    reviewId = route.reviewId,
                    initialRating = route.initialRating,
                    initialContent = route.initialContent,
                    menuLikeInfoList = ReviewModifyPayloadCodec.decode(route.menuLikeInfoPayload),
                    onBack = { navHostController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun ReviewNavPreviewPlaceholder(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = EatssuTheme.typography.subtitle1,
        )
    }
}

@Preview
@Composable
private fun ReviewNavPreview() {
    EatssuTheme {
        ReviewNav(
            menuName = "치킨마요덮밥",
            menuType = MenuType.VARIABLE,
            id = 1L,
        )
    }
}
