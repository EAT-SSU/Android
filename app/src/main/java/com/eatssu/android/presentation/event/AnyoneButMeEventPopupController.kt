package com.eatssu.android.presentation.event

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleCoroutineScope
import com.eatssu.android.R
import com.eatssu.android.data.local.AppFeatureDataStore
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.android.presentation.util.openInBrowser
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.AnyoneButMeAnalyticsEvent
import com.eatssu.common.analytics.PopupAnalyticsEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class AnyoneButMeEventPopupController @Inject constructor(
    @ActivityContext private val context: Context,
    private val appFeatureDataStore: AppFeatureDataStore,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    private val analyticsTracker: AnalyticsTracker,
) {
    private lateinit var composeView: ComposeView
    private lateinit var lifecycleScope: LifecycleCoroutineScope
    private var canAutoShowOnLaunch = false
    private var hasHandledLaunchPopup = false
    private val isPopupVisible = mutableStateOf(false)

    fun bind(
        composeView: ComposeView,
        lifecycleScope: LifecycleCoroutineScope,
        showOnLaunch: Boolean,
    ) {
        this.composeView = composeView
        this.lifecycleScope = lifecycleScope
        canAutoShowOnLaunch = showOnLaunch
        setupContent()
        observePopupState()
    }

    private fun setupContent() {
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        composeView.setContent {
            EatssuTheme {
                if (isPopupVisible.value) {
                    AnyoneButMeEventDialog(
                        onDismiss = ::closeByUser,
                        onDismissForever = ::dismissForever,
                        onInstagramClick = ::openInstagram,
                        onAnyoneButMeClick = { openAnyoneButMePage(fromPopup = true) }
                    )
                }
            }
        }
    }

    private fun observePopupState() {
        lifecycleScope.launch {
            appFeatureDataStore.isAnyoneButMeEventPopupDismissed.collectLatest { dismissed ->
                if (canAutoShowOnLaunch && !hasHandledLaunchPopup && !dismissed) {
                    hasHandledLaunchPopup = true
                    isPopupVisible.value = true
                }
            }
        }
    }

    private fun dismissForever() {
        trackPopupAction(PopupAnalyticsEvent.Action.NOT_SHOW_AGAIN)
        hide()
        lifecycleScope.launch {
            appFeatureDataStore.setAnyoneButMeEventPopupDismissed(true)
        }
    }

    fun openAnyoneButMePage(fromPopup: Boolean = false) {
        if (fromPopup) {
            trackPopupAction(PopupAnalyticsEvent.Action.CLICK_POPUP_IMAGE)
        }
        trackAnyoneButMeClicked()
        hide()
        context.startActivity(
            Intent(context, WebViewActivity::class.java).apply {
                putExtra(WebViewActivity.EXTRA_URL, context.getString(R.string.anyone_but_me_url))
                putExtra(WebViewActivity.EXTRA_TITLE, context.getString(R.string.nav_anyone_but_me))
                putExtra("SCREEN_ID", ScreenId.ANYONE_BUT_ME_MAIN.name)
                putExtra(
                    WebViewActivity.EXTRA_BACK_ICON_RES_ID,
                    com.eatssu.design_system.R.drawable.ic_close
                )
            }
        )
    }

    private fun openInstagram() {
        trackPopupAction(PopupAnalyticsEvent.Action.GO_INSTA)
        hide()
        context.openInBrowser(context.getString(R.string.eatssu_event_instagram_url))
    }

    private fun closeByUser() {
        trackPopupAction(PopupAnalyticsEvent.Action.CLOSE)
        hide()
    }

    private fun trackPopupAction(action: PopupAnalyticsEvent.Action) {
        analyticsTracker.track(PopupAnalyticsEvent.AnyoneButMe(action))
    }

    private fun trackAnyoneButMeClicked() {
        lifecycleScope.launch {
            val userInfo = getUserCollegeDepartmentUseCase()
            analyticsTracker.track(
                AnyoneButMeAnalyticsEvent.Clicked(
                    college = userInfo.userCollege.collegeId.toLong(),
                    major = userInfo.userDepartment.departmentId.toLong(),
                ),
            )
        }
    }

    private fun hide() {
        canAutoShowOnLaunch = false
        isPopupVisible.value = false
    }
}
