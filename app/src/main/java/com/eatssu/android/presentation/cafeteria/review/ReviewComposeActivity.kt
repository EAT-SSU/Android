package com.eatssu.android.presentation.cafeteria.review

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.R
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.usecase.alarm.AlarmUseCase
import com.eatssu.android.domain.usecase.alarm.SetDailyNotificationStatusUseCase
import com.eatssu.android.presentation.MainActivity
import com.eatssu.android.presentation.favorite.FavoriteFragment
import com.eatssu.android.presentation.favorite.todayMenuRestaurants
import com.eatssu.android.presentation.util.openAppNotificationSettings
import com.eatssu.android.presentation.util.showDialog
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReviewComposeActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var settingDataStore: SettingDataStore

    @Inject
    lateinit var setNotificationStatus: SetDailyNotificationStatusUseCase

    @Inject
    lateinit var alarmUseCase: AlarmUseCase

    private var notificationDialog: Dialog? = null
    private var isCheckingNotification = false
    private var menuDate: LocalDate? = null
    private var mealTime: Time? = null
    private var menuType: String? = null
    private var restaurant: String? = null
    private var itemId by Delegates.notNull<Long>()
    private lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        getIntents() // 컴포즈 화면 그리기 전에 호출

        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    val navHostController = rememberNavController()
                    val parsedMenuType = MenuType.entries.find { it.name == menuType }
                    val parsedRestaurant = Restaurant.entries.find { it.name == restaurant }

                    if (parsedMenuType != null && parsedRestaurant != null) {
                        ReviewNav(
                            navHostController = navHostController,
                            menuType = parsedMenuType,
                            restaurant = parsedRestaurant,
                            menuName = itemName,
                            id = itemId,
                            menuDate = menuDate,
                            onMenuFavoriteClick = ::openMenuFavorites,
                            onExit = { finish() }
                        )
                    } else {
                        Timber.e("Invalid review parameters received: menuType=$menuType, restaurant=$restaurant")
                        ErrorScreen(
                            onBackClick = { finish() }
                        )
                    }
                }
            }
        }
    }

    private fun getIntents() { //todo 추후 변경
        menuDate = intent.getStringExtra(EXTRA_MENU_DATE)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        mealTime = intent.getStringExtra(EXTRA_MEAL_TIME)?.let { value ->
            Time.entries.firstOrNull { it.name == value }
        }
        menuType = intent.getStringExtra("menuType")
        restaurant = intent.getStringExtra("restaurant")
        itemId = intent.getLongExtra("itemId", 0)
        itemName = intent.getStringExtra("itemName").toString().replace(Regex("[\\[\\]]"), "")

        Timber.d("메뉴는 $itemName $menuType $itemId")
    }

    private fun openMenuFavorites() {
        if (Restaurant.entries.firstOrNull { it.name == restaurant } !in todayMenuRestaurants) return
        if (menuDate != LocalDate.now() || isCheckingNotification || notificationDialog?.isShowing == true) return
        isCheckingNotification = true
        lifecycleScope.launch {
            try {
                val notificationsEnabled = settingDataStore.dailyNotificationStatus.first()
                // Recheck after the suspending read in case the calendar day changed.
                if (menuDate != LocalDate.now()) return@launch
                if (!NotificationManagerCompat.from(this@ReviewComposeActivity)
                        .areNotificationsEnabled()
                ) {
                    notificationDialog = showDialog(
                        title = getString(R.string.dialog_notification_permission_title),
                        description = getString(R.string.dialog_notification_permission_description),
                    ) {
                        confirmText = getString(R.string.dialog_settings)
                        onConfirm { dialog ->
                            dialog.dismiss()
                            openAppNotificationSettings()
                        }
                    }
                } else if (!notificationsEnabled) {
                    notificationDialog = showDialog(
                        title = getString(R.string.dialog_notification_permission_title),
                        description = getString(R.string.review_menu_notification_description),
                    ) {
                        confirmText = getString(R.string.button_allow_notification)
                        onConfirm { dialog ->
                            lifecycleScope.launch {
                                setNotificationStatus(true)
                                alarmUseCase.scheduleAlarm()
                            }
                            dialog.dismiss()
                        }
                    }
                } else {
                    startActivity(
                        Intent(
                            this@ReviewComposeActivity,
                            MainActivity::class.java
                        ).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            putExtra(MainActivity.EXTRA_OPEN_MENU_FAVORITES, true)
                            putExtra(FavoriteFragment.ARG_TODAY_RESTAURANT, restaurant)
                            putExtra(FavoriteFragment.ARG_TODAY_TIME, mealTime?.name)
                            putExtra(FavoriteFragment.ARG_TODAY_EXPANDED, true)
                        })
                }
            } finally {
                isCheckingNotification = false
            }
        }
    }

    override fun onDestroy() {
        notificationDialog?.dismiss()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_MEAL_TIME = "review_meal_time"
        const val EXTRA_MENU_DATE = "review_menu_date"
    }

    @Composable
    private fun ErrorScreen(onBackClick: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.review_menu_info_load_failed),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text(text = stringResource(R.string.nav_back))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun ErrorScreenPreview() {
        EatssuTheme {
            ErrorScreen(onBackClick = {})
        }
    }
}
