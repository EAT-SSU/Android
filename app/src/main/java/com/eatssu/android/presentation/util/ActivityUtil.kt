package com.eatssu.android.presentation.util

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

@Deprecated(
    message = "Use Activity companion object pattern instead. Example: MainActivity.start(context)",
    replaceWith = ReplaceWith("ActivityName.start(this)"),
    level = DeprecationLevel.WARNING
)
inline fun <reified T : Activity> AppCompatActivity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}