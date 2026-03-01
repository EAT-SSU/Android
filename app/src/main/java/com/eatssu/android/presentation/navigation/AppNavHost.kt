package com.eatssu.android.presentation.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.R
import com.eatssu.android.presentation.MainRoute
import com.eatssu.android.presentation.cafeteria.review.ReviewNav
import com.eatssu.android.presentation.cafeteria.review.report.ReportRoute
import com.eatssu.android.presentation.intro.IntroRoute
import com.eatssu.android.presentation.login.LoginRoute
import com.eatssu.android.presentation.mypage.DeveloperScreen
import com.eatssu.android.presentation.mypage.SignOutRoute
import com.eatssu.android.presentation.mypage.language.LanguageSelectorRoute
import com.eatssu.android.presentation.mypage.myreview.MyReviewNav
import com.eatssu.android.presentation.mypage.terms.WebViewScreen
import com.eatssu.android.presentation.mypage.userinfo.UserInfoRoute
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.EatssuDialog
import com.eatssu.design_system.theme.EatssuTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient

@Composable
fun EatssuAppNavHost(
    launchPath: String?,
    onFinishApp: () -> Unit,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = AppDestination.Intro,
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable<AppDestination.Intro> {
            IntroRoute(
                launchPath = launchPath,
                onNavigateToMain = {
                    navController.navigateToMainClearingBackStack()
                },
                onNavigateToLogin = {
                    navController.navigateToLoginClearingBackStack()
                },
                onForceUpdate = {
                    navController.navigate(AppDestination.ForceUpdate)
                },
            )
        }

        composable<AppDestination.ForceUpdate> {
            EatssuDialog(
                title = context.getString(R.string.title_force_update),
                description = context.getString(R.string.dialog_force_update_message),
                confirmText = context.getString(R.string.button_update),
                showCancelButton = false,
                cancellable = false,
                onConfirm = {
                    openPlayStore(context)
                },
            )
        }

        composable<AppDestination.Login> {
            LoginRoute(
                onNavigateToMain = {
                    navController.navigateToMainClearingBackStack()
                },
                onBackPress = onFinishApp,
            )
        }

        composable<AppDestination.Main> {
            MainRoute(
                appNavController = navController,
                onNavigateToUserInfo = { force ->
                    navController.navigate(AppDestination.UserInfo(force))
                },
                onNavigateToLogin = {
                    navController.navigateToLoginClearingBackStack()
                },
                onNavigateToReview = { menuType, itemId, itemName ->
                    navController.navigate(
                        AppDestination.Review(
                            menuType = menuType,
                            itemId = itemId,
                            itemName = itemName,
                        )
                    )
                },
                onNavigateToInquire = {
                    openInquireChat(context)
                },
                onNavigateToDeveloper = {
                    navController.navigate(AppDestination.Developer)
                },
                onNavigateToOss = {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                },
            )
        }

        composable<AppDestination.UserInfo> { backStackEntry ->
            val route = backStackEntry.toRoute<AppDestination.UserInfo>()
            UserInfoRoute(
                onBack = {
                    if (route.force) {
                        navController.navigateToMainClearingBackStack()
                    } else {
                        navController.popBackStack()
                    }
                },
            )
        }

        composable<AppDestination.WebView> { backStackEntry ->
            val route = backStackEntry.toRoute<AppDestination.WebView>()
            val decodedTitle = Uri.decode(route.title)
            val decodedUrl = Uri.decode(route.url)

            var reloadKey by rememberSaveable { mutableIntStateOf(0) }

            key(reloadKey) {
                WebViewScreen(
                    title = decodedTitle,
                    url = decodedUrl,
                    screenId = route.screenId,
                    backIconResId = route.backIconResId,
                    onBack = { navController.popBackStack() },
                    onRecreate = { reloadKey += 1 },
                )
            }
        }

        composable<AppDestination.MyReview> {
            MyReviewNav(
                onExit = { navController.popBackStack() },
            )
        }

        composable<AppDestination.Review> { backStackEntry ->
            val route = backStackEntry.toRoute<AppDestination.Review>()

            if (route.itemId < 0L) {
                navController.popBackStack()
            } else {
                ReviewNav(
                    menuName = route.itemName,
                    menuType = route.menuType,
                    id = route.itemId,
                    onExit = { navController.popBackStack() },
                    onNavigateToReport = { reviewId ->
                        navController.navigate(AppDestination.Report(reviewId))
                    },
                )
            }
        }

        composable<AppDestination.Report> { backStackEntry ->
            val route = backStackEntry.toRoute<AppDestination.Report>()
            if (route.reviewId < 0L) {
                navController.popBackStack()
            } else {
                ReportRoute(
                    reviewId = route.reviewId,
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable<AppDestination.SignOut> { backStackEntry ->
            val route = backStackEntry.toRoute<AppDestination.SignOut>()
            SignOutRoute(
                nickname = route.nickname,
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigateToLoginClearingBackStack()
                },
            )
        }

        composable<AppDestination.Developer> {
            DeveloperScreen(
                onBack = { navController.popBackStack() },
                onRecruitingClick = {
                    navController.navigate(
                        AppDestination.WebView(
                            url = context.getString(R.string.recruiting_url),
                            title = "Who's next?",
                            screenId = ScreenId.EXTERNAL_RECRUIT,
                        )
                    )
                },
            )
        }

        composable<AppDestination.Language> {
            LanguageSelectorRoute(
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Preview
@Composable
private fun EatssuAppNavHostPreview() {
    EatssuTheme {
        EatssuAppNavHost(
            launchPath = null,
            onFinishApp = {},
            startDestination = AppDestination.ForceUpdate,
        )
    }
}

internal fun NavHostController.navigateToWebView(
    url: String,
    title: String,
    screenId: ScreenId,
    backIconResId: Int = -1,
) {
    navigate(
        AppDestination.WebView(
            url = url,
            title = title,
            screenId = screenId,
            backIconResId = backIconResId,
        )
    )
}

internal fun NavHostController.navigateToMyReview() {
    navigate(AppDestination.MyReview)
}

internal fun NavHostController.navigateToSignOut(nickname: String?) {
    navigate(AppDestination.SignOut(nickname.orEmpty()))
}

private fun NavHostController.navigateToMainClearingBackStack() {
    navigate(AppDestination.Main) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToLoginClearingBackStack() {
    navigate(AppDestination.Login) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

private fun openInquireChat(context: Context) {
    val channelPublicId = "_ZlVAn"
    TalkApiClient.instance.chatChannel(context, channelPublicId) {
        val url = TalkApiClient.instance.chatChannelUrl(channelPublicId)
        KakaoCustomTabsClient.openWithDefault(context, url)
    }
    EventLogger.screenView(ScreenId.EXTERNAL_INQUIRE)
}

private fun openPlayStore(context: Context) {
    val packageName = context.packageName
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName"),
            )
        )
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
            )
        )
    }
}
