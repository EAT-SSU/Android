package com.eatssu.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eatssu.android.presentation.navigation.EatssuAppNavHost
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val launchPath = intent.getStringExtra("launch_path")

        setContent {
            EatssuTheme {
                EatssuAppNavHost(
                    launchPath = launchPath,
                    onFinishApp = { finishAffinity() },
                )
            }
        }
    }
}
