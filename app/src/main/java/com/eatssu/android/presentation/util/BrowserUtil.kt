package com.eatssu.android.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openInBrowser(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        if (this@openInBrowser !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    findBrowserPackage(browserIntent)?.let(browserIntent::setPackage)
    startActivity(browserIntent)
}

private fun Context.findBrowserPackage(intent: Intent): String? {
    val browserPackages = packageManager.queryIntentActivities(
        Intent(Intent.ACTION_VIEW, "https://www.google.com".toUri()).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        },
        0
    ).map { resolveInfo ->
        resolveInfo.activityInfo.packageName
    }.toSet()

    return packageManager.queryIntentActivities(intent, 0)
        .firstOrNull { resolveInfo ->
            resolveInfo.activityInfo.packageName in browserPackages &&
                resolveInfo.activityInfo.packageName != packageName
        }
        ?.activityInfo
        ?.packageName
}
