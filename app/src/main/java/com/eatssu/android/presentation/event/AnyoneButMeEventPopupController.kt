package com.eatssu.android.presentation.event

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleCoroutineScope
import com.eatssu.android.data.local.AppFeatureDataStore
import com.eatssu.design_system.theme.EatssuTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnyoneButMeEventPopupController(
    private val composeView: ComposeView,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val appFeatureDataStore: AppFeatureDataStore,
    private val onOpenAnyoneButMe: () -> Unit,
    private val onOpenInstagram: () -> Unit,
) {
    private var canAutoShowOnLaunch = false
    private var hasHandledLaunchPopup = false
    private val isPopupVisible = mutableStateOf(false)

    fun bind(showOnLaunch: Boolean) {
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
                        onAnyoneButMeClick = ::openAnyoneButMe
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

    private fun openInstagram() {
        hide()
        onOpenInstagram()
    }

    private fun openAnyoneButMe() {
        hide()
        onOpenAnyoneButMe()
    }

    private fun hide() {
        canAutoShowOnLaunch = false
        isPopupVisible.value = false
    }
}
