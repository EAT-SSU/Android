package com.eatssu.android.presentation.mypage.myreview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyReviewListComposeActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    val navHostController = rememberNavController()

                    MyReviewNav(
                        navHostController = navHostController,
                        onExit = { finish() }
                    )
                }
            }
        }
    }
}
