package com.eatssu.android.presentation.widget.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.launchApp() {
    val deepLinkUri = Uri.parse("eatssu://root")
    val intent = Intent(Intent.ACTION_VIEW, deepLinkUri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    // 위젯에서 온 것을 표시하는 플래그 추가
    intent.putExtra("launch_path", "widget")

    runCatching { this.startActivity(intent) }
}