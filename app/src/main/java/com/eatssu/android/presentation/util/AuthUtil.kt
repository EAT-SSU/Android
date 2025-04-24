package com.eatssu.android.presentation.util

import android.content.Context
import android.content.Intent
import com.eatssu.android.R
import com.eatssu.android.presentation.login.LoginActivity

fun handleExpiredRefreshToken(context: Context) {
    context.showToast(context.getString(R.string.login_failed))

    val intent = Intent(context, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}