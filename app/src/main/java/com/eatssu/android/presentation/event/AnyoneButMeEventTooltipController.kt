package com.eatssu.android.presentation.event

import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.doOnLayout
import com.eatssu.design_system.theme.EatssuTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AnyoneButMeEventTooltipController @Inject constructor() {
    private companion object {
        const val BOTTOM_NAVIGATION_ITEM_COUNT = 4
        const val ANYONE_BUT_ME_MENU_INDEX = 2
    }

    private lateinit var tooltipComposeView: ComposeView
    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavigationLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        updateTooltipPosition()
    }
    private val tooltipLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        updateTooltipPosition()
    }

    fun bind(
        tooltipComposeView: ComposeView,
        bottomNavigationView: BottomNavigationView,
    ) {
        clearPreviousBindings()

        this.tooltipComposeView = tooltipComposeView
        this.bottomNavigationView = bottomNavigationView

        setupContent()
        observeLayout()
    }

    private fun clearPreviousBindings() {
        if (::bottomNavigationView.isInitialized) {
            bottomNavigationView.removeOnLayoutChangeListener(bottomNavigationLayoutChangeListener)
        }

        if (::tooltipComposeView.isInitialized) {
            tooltipComposeView.removeOnLayoutChangeListener(tooltipLayoutChangeListener)
        }
    }

    private fun setupContent() {
        tooltipComposeView.apply {
            isClickable = false
            isFocusable = false
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                EatssuTheme {
                    AnyoneButMeEventTooltip()
                }
            }
        }
    }

    private fun observeLayout() {
        bottomNavigationView.addOnLayoutChangeListener(bottomNavigationLayoutChangeListener)
        tooltipComposeView.addOnLayoutChangeListener(tooltipLayoutChangeListener)

        bottomNavigationView.doOnLayout { updateTooltipPosition() }
        tooltipComposeView.doOnLayout { updateTooltipPosition() }
        tooltipComposeView.post { updateTooltipPosition() }
    }

    private fun updateTooltipPosition() {
        if (!::tooltipComposeView.isInitialized || !::bottomNavigationView.isInitialized) return
        if (tooltipComposeView.width == 0 || tooltipComposeView.height == 0) return

        val itemWidth = bottomNavigationView.width / BOTTOM_NAVIGATION_ITEM_COUNT.toFloat()
        val itemCenterX = itemWidth * ANYONE_BUT_ME_MENU_INDEX + (itemWidth / 2f)

        tooltipComposeView.x =
            bottomNavigationView.x +
                itemCenterX -
                (tooltipComposeView.width / 2f)
        tooltipComposeView.y =
            bottomNavigationView.y - tooltipComposeView.height.toFloat()
        tooltipComposeView.visibility = View.VISIBLE
    }
}
