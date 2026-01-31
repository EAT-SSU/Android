package com.eatssu.android.presentation.mypage.myreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyReviewListComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
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