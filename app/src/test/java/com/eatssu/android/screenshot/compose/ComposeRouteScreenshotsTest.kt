package com.eatssu.android.screenshot.compose
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListScreen
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewScreen
import com.eatssu.android.presentation.cafeteria.review.write.WriteReviewScreen
import com.eatssu.android.presentation.map.MapScreen
import com.eatssu.android.presentation.mypage.language.LanguageSelectorContent
import com.eatssu.android.presentation.mypage.myreview.MyReviewListScreen
import com.eatssu.android.presentation.widget.ui.WidgetSettingScreen
import com.eatssu.android.screenshot.core.ScreenshotCapture
import com.eatssu.android.screenshot.core.ScreenshotDeterminismRule
import com.eatssu.android.screenshot.core.ScreenshotTestApplication
import com.eatssu.android.screenshot.fixtures.FakeUiStates
import com.eatssu.android.screenshot.inventory.ScreenCoverageItem
import com.eatssu.android.screenshot.inventory.ScreenCoverageRegistry
import com.eatssu.common.enums.AppLanguage
import com.eatssu.design_system.theme.EatssuTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    application = ScreenshotTestApplication::class,
    sdk = [35],
    qualifiers = "ko-rKR-w411dp-h891dp-xxhdpi",
)
class ComposeRouteScreenshotsTest {
    private val determinismRule = ScreenshotDeterminismRule()
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(determinismRule).around(composeRule)

    @Test
    fun captureAllComposeAndRouteScreens() {
        var currentItem by mutableStateOf<ScreenCoverageItem?>(null)
        var currentState by mutableStateOf("")
        val targets = ScreenCoverageRegistry.itemsFor("compose/ComposeRouteScreenshotsTest.kt")

        composeRule.setContent {
            ScreenshotHost {
                val item = currentItem
                if (item != null) {
                    RenderTarget(item, currentState)
                }
            }
        }

        targets
            .forEach { item ->
                item.states.forEach { state ->
                    composeRule.runOnIdle {
                        currentItem = item
                        currentState = state
                    }
                    composeRule.waitForIdle()
                    ScreenshotCapture.captureComposeRoot(
                        type = typeOf(item.targetId),
                        target = ScreenCoverageRegistry.screenshotTargetName(item.targetId),
                        state = state,
                        composeRule = composeRule,
                    )
                }
            }
    }

    @Composable
    private fun ScreenshotHost(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Ltr,
            LocalDensity provides Density(density = 2.625f, fontScale = 1f),
        ) {
            EatssuTheme {
                Box(
                    modifier = Modifier
                        .requiredSize(width = 411.dp, height = 891.dp)
                        .background(androidx.compose.ui.graphics.Color.White)
                ) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun RenderTarget(item: ScreenCoverageItem, state: String) {
        when (item.targetId) {
            "activity:.presentation.cafeteria.review.ReviewComposeActivity",
            "route:ReviewNav.List" -> renderReviewList(state)

            "activity:.presentation.mypage.language.LanguageSelectorActivity" -> {
                LanguageSelectorContent(
                    selectedLanguage = AppLanguage.KOREAN,
                    onLanguageSelected = {},
                    onBack = {},
                )
            }

            "activity:.presentation.mypage.myreview.MyReviewListComposeActivity",
            "route:MyReviewNav.List" -> {
                MyReviewListScreen(
                    uiState = FakeUiStates.myReviewUiState(state),
                    userNickname = "eatssu_user",
                    onBack = {},
                    onModifyClick = {},
                    onDeleteClick = {},
                )
            }

            "activity:.presentation.widget.ui.WidgetSettingActivity" -> {
                val optionList = when (state) {
                    "empty" -> emptyList()
                    else -> listOf("학생 식당", "도담 식당", "기숙사 식당")
                }
                val selected = optionList.firstOrNull().orEmpty()
                WidgetSettingScreen(
                    restaurantOptionList = optionList,
                    selectedRestaurant = selected,
                    onSelectRestaurant = {},
                    onConfirm = {},
                    onBack = {},
                )
            }

            "fragment:com.eatssu.android.presentation.map.MapFragment" -> renderMap(state)
            "route:ReviewNav.Write" -> renderWrite(state)
            "route:ReviewNav.Modify",
            "route:MyReviewNav.Modify" -> renderModify(state)

            else -> PlaceholderScreen("unsupported: ${item.targetId}")
        }
    }

    @Composable
    private fun renderReviewList(state: String) {
        val pagingDataFlow = remember(state) {
            flowOf(FakeUiStates.reviewPagingData(state))
        }
        val pagingItems = pagingDataFlow.collectAsLazyPagingItems()

        ReviewListScreen(
            uiState = FakeUiStates.reviewListUiState(state),
            reviewPagingItems = pagingItems,
            menuName = "돈까스",
            onBack = {},
            onReviewWriteButtonClick = {},
            onModifyClick = {},
            onDeleteClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Composable
    private fun renderWrite(state: String) {
        val model = FakeUiStates.writeReviewModel(state)

        WriteReviewScreen(
            title = "리뷰 작성",
            menuList = model.menuList,
            rating = model.rating,
            content = model.content,
            likedMenuIds = model.likedMenuIds,
            selectedImageUri = model.selectedImageUri,
            isPosting = model.isPosting,
            onBack = {},
            onRatingChanged = {},
            onContentChanged = {},
            onToggleLike = {},
            onImageSelect = {},
            onImageDelete = {},
            onSubmit = {},
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Composable
    private fun renderModify(state: String) {
        val model = FakeUiStates.modifyReviewModel(state)

        ModifyReviewScreen(
            title = "리뷰 수정",
            rating = model.rating,
            content = model.content,
            menuLikeInfos = model.menuLikeInfos,
            isSubmitting = model.isSubmitting,
            canSubmit = model.canSubmit,
            onBack = {},
            onRatingChanged = {},
            onContentChanged = {},
            onToggleLike = {},
            onSubmit = {},
            modifier = Modifier.fillMaxSize(),
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun renderMap(state: String) {
        val mapState = FakeUiStates.mapState(state)
        val departmentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val partnershipSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        MapScreen(
            mapState = mapState,
            cameraPositionState = null,
            locationSource = null,
            departmentSheetState = departmentSheetState,
            partnershipSheetState = partnershipSheetState,
            showToast = { _, _ -> },
            navigateToUserInfo = {},
            onHideDepartmentSheet = {},
            onHidePartnershipSheet = {},
            animateCameraPositionTo = { _, _ -> },
            onSelectPartnershipByStoreName = { _, _ -> },
            onSelectedFilterChange = {},
            departmentId = 1L,
            collegeId = 1L,
            departmentName = "컴퓨터학부",
            selectedFilter = mapState.selectedFilter,
            useDeterministicRenderer = true,
            deterministicStateLabel = state,
        )
    }

    @Composable
    private fun PlaceholderScreen(text: String) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color(0xFFF8FAFC))
        ) {
            androidx.compose.material3.Text(
                text = text,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        }
    }

    private fun typeOf(targetId: String): String = targetId.substringBefore(':')
}
