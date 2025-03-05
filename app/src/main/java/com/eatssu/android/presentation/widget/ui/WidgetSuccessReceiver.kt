package com.eatssu.android.presentation.widget.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eatssu.android.presentation.widget.EventLogger

class WidgetSuccessReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val widgetType = intent?.getStringExtra("widgetType") ?: return

        EventLogger.completeAddToWidget(widgetType)
    }
}