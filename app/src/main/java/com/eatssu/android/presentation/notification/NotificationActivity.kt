package com.eatssu.android.presentation.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private val viewModel: NotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EatssuTheme {
                val items = viewModel.notifications.collectAsStateWithLifecycle(initialValue = null)
                NotificationScreen(
                    notifications = items.value,
                    onBack = { finish() },
                    onMarkAllRead = viewModel::markAllRead,
                    onNotificationClick = { notification ->
                        lifecycleScope.launch {
                            viewModel.markRead(notification.id)
                            setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra(EXTRA_MEAL_DATE, notification.mealDate)
                                putExtra(EXTRA_MEAL_TIME, notification.mealTime.name)
                            })
                            finish()
                        }
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_MEAL_DATE = "notification_meal_date"
        const val EXTRA_MEAL_TIME = "notification_meal_time"
    }
}
