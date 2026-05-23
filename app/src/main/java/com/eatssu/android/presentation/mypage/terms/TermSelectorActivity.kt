package com.eatssu.android.presentation.mypage.terms

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ClickMyPageMenuEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TermSelectorActivity : ComponentActivity() {

    companion object {
        private const val MENU_TERMS_OF_USE = "terms_of_use"
        private const val MENU_PRIVACY_POLICY = "privacy_policy"
    }

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {
                TermSelectorScreen(
                    onBack = { finish() },
                    onServiceRuleClick = {
                        trackMyPageMenu(MENU_TERMS_OF_USE)
                        startWebView(
                            getString(R.string.terms_url),
                            getString(R.string.terms),
                            ScreenId.EXTERNAL_TERMS
                        )
                    },
                    onPrivateInformationClick = {
                        trackMyPageMenu(MENU_PRIVACY_POLICY)
                        startWebView(
                            getString(R.string.policy_url),
                            getString(R.string.policy),
                            ScreenId.EXTERNAL_POLICY
                        )
                    },
                )
            }

        }
    }

    private fun startWebView(url: String, title: String, screenId: ScreenId) {
        val intent = Intent(applicationContext, WebViewActivity::class.java).apply {
            putExtra(WebViewActivity.EXTRA_URL, url)
            putExtra(WebViewActivity.EXTRA_TITLE, title)
            putExtra("SCREEN_ID", screenId.name)
        }
        startActivity(intent)
    }

    private fun trackMyPageMenu(menu: String) {
        lifecycleScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            analyticsTracker.track(
                ClickMyPageMenuEvent(
                    college = userCollegeDepartment.userCollege.collegeId.toLong(),
                    major = userCollegeDepartment.userDepartment.departmentId.toLong(),
                    menu = menu,
                ),
            )
        }
    }
}
