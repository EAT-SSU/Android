package com.eatssu.android.presentation.mypage.language

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageSelectorActivity : ComponentActivity() {

    companion object {
        private const val KEY_LANGUAGE_CHANGED = "KEY_LANGUAGE_CHANGED"
    }

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val viewModel: LanguageSelectorViewModel by viewModels()
    private var hasLanguageChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        hasLanguageChanged = savedInstanceState?.getBoolean(KEY_LANGUAGE_CHANGED) ?: false
        updateResultIfNeeded()
        collectLanguageChanged()
        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    LanguageSelectorScreen(
                        viewModel = viewModel,
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_LANGUAGE_CHANGED, hasLanguageChanged)
        super.onSaveInstanceState(outState)
    }

    private fun collectLanguageChanged() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.languageChanged.collect {
                    hasLanguageChanged = true
                    updateResultIfNeeded()
                }
            }
        }
    }

    private fun updateResultIfNeeded() {
        if (hasLanguageChanged) {
            setResult(RESULT_OK)
        }
    }
}
