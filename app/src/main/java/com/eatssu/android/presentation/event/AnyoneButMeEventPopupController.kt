package com.eatssu.android.presentation.event

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleCoroutineScope
import com.eatssu.android.R
import com.eatssu.android.data.local.AppFeatureDataStore
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.android.presentation.util.openInBrowser
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
                        onDismiss = ::hide,
                        onDismissForever = ::dismissForever,
                        onInstagramClick = ::openInstagram,
                        onAnyoneButMeClick = ::openAnyoneButMePage
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
        hide()
        lifecycleScope.launch {
            appFeatureDataStore.setAnyoneButMeEventPopupDismissed(true)
        }
    }

    fun openAnyoneButMePage() {
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
        hide()
        context.openInBrowser(context.getString(R.string.eatssu_event_instagram_url))
    }

    private fun hide() {
        canAutoShowOnLaunch = false
        isPopupVisible.value = false
    }
}
