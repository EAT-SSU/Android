package com.eatssu.notification.navigation

import android.content.Intent

interface NotificationNavigationProvider {
    fun getLaunchIntent(): Intent
}
