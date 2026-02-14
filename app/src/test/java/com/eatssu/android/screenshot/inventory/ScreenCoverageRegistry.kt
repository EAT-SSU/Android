package com.eatssu.android.screenshot.inventory

data class ScreenCoverageItem(
    val targetId: String,
    val states: Set<String>,
    val testFile: String,
)

object ScreenCoverageRegistry {
    val excludedTargets: Map<String, String> = mapOf(
        "activity:com.kakao.sdk.auth.AuthCodeHandlerActivity" to "Third-party SDK activity",
    )

    val coverageItems: List<ScreenCoverageItem> = listOf(
        // Activity targets
        ScreenCoverageItem(
            targetId = "activity:.presentation.intro.IntroActivity",
            states = setOf("success"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.login.LoginActivity",
            states = setOf("loading", "success"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.MainActivity",
            states = setOf("success_cafeteria", "success_map", "success_mypage"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.cafeteria.review.report.ReportActivity",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.common.AndroidMessageDialogActivity",
            states = setOf("success"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.common.ForceUpdateDialogActivity",
            states = setOf("success"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.mypage.DeveloperActivity",
            states = setOf("success"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.mypage.SignOutActivity",
            states = setOf("empty", "success", "error"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.mypage.terms.WebViewActivity",
            states = setOf("loading", "success", "error"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.mypage.userinfo.UserInfoActivity",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "xml/ActivityScreenSnapshotsTest.kt"
        ),

        // Fragment targets
        ScreenCoverageItem(
            targetId = "fragment:com.eatssu.android.presentation.cafeteria.CafeteriaFragment",
            states = setOf("loading", "success"),
            testFile = "xml/FragmentScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "fragment:com.eatssu.android.presentation.cafeteria.menu.MenuFragment",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "xml/FragmentScreenSnapshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "fragment:com.eatssu.android.presentation.mypage.MyPageFragment",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "xml/FragmentScreenSnapshotsTest.kt"
        ),

        // Compose-backed Activity and Route targets
        ScreenCoverageItem(
            targetId = "activity:.presentation.cafeteria.review.ReviewComposeActivity",
            states = setOf("loading", "success"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.mypage.language.LanguageSelectorActivity",
            states = setOf("success"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.mypage.myreview.MyReviewListComposeActivity",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "activity:.presentation.widget.ui.WidgetSettingActivity",
            states = setOf("empty", "success"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "fragment:com.eatssu.android.presentation.map.MapFragment",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "route:ReviewNav.List",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "route:ReviewNav.Write",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "route:ReviewNav.Modify",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "route:MyReviewNav.List",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
        ScreenCoverageItem(
            targetId = "route:MyReviewNav.Modify",
            states = setOf("loading", "empty", "success", "error"),
            testFile = "compose/ComposeRouteScreenshotsTest.kt"
        ),
    )

    val coveredTargetIds: Set<String> = coverageItems.map { it.targetId }.toSet()

    fun itemsFor(testFile: String): List<ScreenCoverageItem> =
        coverageItems.filter { it.testFile == testFile }

    fun screenshotTargetName(targetId: String): String {
        return targetId.substringAfter(':')
            .removePrefix(".")
            .removePrefix("com.eatssu.android.")
            .replace('.', '_')
    }
}
