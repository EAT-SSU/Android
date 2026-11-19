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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.eatssu.android.R
import android.widget.Toast
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
                val items = viewModel.notifications.collectAsStateWithLifecycle()
                val loading by viewModel.loading.collectAsStateWithLifecycle()
                val error by viewModel.error.collectAsStateWithLifecycle()
                val actionFailed by viewModel.actionFailed.collectAsStateWithLifecycle()
                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
                LaunchedEffect(actionFailed) {
                    if (actionFailed) {
                        Toast.makeText(this@NotificationActivity, R.string.notification_read_error, Toast.LENGTH_SHORT).show()
                        viewModel.actionFailed.value = false
                    }
                }
                NotificationScreen(
                    notifications = if (loading && items.value.isEmpty()) null else items.value,
                    hasError = error,
                    onRetry = viewModel::refresh,
                    onBack = { finish() },
                    onMarkAllRead = viewModel::markAllRead,
                    onNotificationClick = { notification ->
                        lifecycleScope.launch {
                            if (!viewModel.markRead(notification.id)) {
                                Toast.makeText(this@NotificationActivity, R.string.notification_read_error, Toast.LENGTH_SHORT).show()
                            }
                            setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra(EXTRA_OPEN_CAFETERIA, true)
                                putExtra(EXTRA_RESTAURANT, notification.targetRestaurant?.name)
                                putExtra(EXTRA_MEAL_DATE, notification.mealDate)
                                putExtra(EXTRA_MEAL_TIME, notification.mealTime?.name)
                            })
                            finish()
                        }
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_OPEN_CAFETERIA = "notification_open_cafeteria"
        const val EXTRA_RESTAURANT = "notification_restaurant"
        const val EXTRA_MEAL_DATE = "notification_meal_date"
        const val EXTRA_MEAL_TIME = "notification_meal_time"
    }
}
