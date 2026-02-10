package com.eatssu.android.navigation

import android.content.Context
import android.content.Intent
import com.eatssu.android.presentation.intro.IntroActivity
import com.eatssu.notification.navigation.NotificationNavigationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppNotificationNavigationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationNavigationProvider {
    override fun getLaunchIntent(): Intent {
        return Intent(context, IntroActivity::class.java)
    }
}
