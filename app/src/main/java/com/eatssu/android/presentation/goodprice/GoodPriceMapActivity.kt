package com.eatssu.android.presentation.goodprice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 착한가격업소 지도 액티비티 (비로그인 사용자도 진입 가능)
 */
@AndroidEntryPoint
class GoodPriceMapActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 애널리틱스 트래커 및 EatssuTheme 적용
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    GoodPriceMapRoute()
                }
            }
        }
    }
}
