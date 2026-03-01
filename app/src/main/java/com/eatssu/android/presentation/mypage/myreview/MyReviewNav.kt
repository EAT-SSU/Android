package com.eatssu.android.presentation.mypage.myreview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.eatssu.design_system.preview.ThemePreviews
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewRoute
import com.eatssu.android.presentation.navigation.MyReviewDestination
import com.eatssu.android.presentation.navigation.ReviewModifyPayloadCodec
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun MyReviewNav(
    navHostController: NavHostController = rememberNavController(),
    onExit: () -> Unit = {},
) {
    val isPreviewMode = LocalInspectionMode.current

    NavHost(
        navController = navHostController,
        startDestination = MyReviewDestination.List,
    ) {
        composable<MyReviewDestination.List> {
            if (isPreviewMode) {
                MyReviewNavPreviewPlaceholder(title = "My Review List")
            } else {
                MyReviewListRoute(
                    onBack = { onExit() },
                    onModifyClick = { review ->
                        navHostController.navigate(
                            MyReviewDestination.Modify(
                                reviewId = review.reviewId,
                                initialRating = review.rating,
                                initialContent = review.content,
                                menuLikeInfoPayload = ReviewModifyPayloadCodec.encode(review.menuLikeInfoList),
                            )
                        ) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        }

        composable<MyReviewDestination.Modify> { backStackEntry ->
            if (isPreviewMode) {
                MyReviewNavPreviewPlaceholder(title = "Modify My Review")
            } else {
                val route = backStackEntry.toRoute<MyReviewDestination.Modify>()

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
private fun MyReviewNavPreviewPlaceholder(title: String) {
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

@ThemePreviews
@Composable
private fun MyReviewNavPreview() {
    EatssuTheme {
        MyReviewNav()
    }
}
