package com.eatssu.android.presentation.mypage.terms

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.eatssu.android.R
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme

class TermSelectorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {
                TermSelectorScreen(
                    onBack = { finish() },
                    onServiceRuleClick = {
                        startWebView(
                            getString(R.string.terms_url),
                            getString(R.string.terms),
                            ScreenId.EXTERNAL_TERMS
                        )
                    },
                    onPrivateInformationClick = {
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
}