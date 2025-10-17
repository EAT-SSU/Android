package com.eatssu.android.presentation.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import kotlin.reflect.KClass

abstract class ActivityCompanion<IntentOptions>(
    private val activityClass: KClass<out Activity>,
    private val optionsClass: KClass<IntentOptions>
) where IntentOptions : Parcelable {
    companion object {
        private const val INTENT_OPTIONS_KEY = "intent_options"
    }

    fun intent(
        context: Context,
        options: IntentOptions,
        intentBuilder: Intent.() -> Unit = {}
    ): Intent =
        Intent(context, activityClass.java).apply {
            putExtra(INTENT_OPTIONS_KEY, options)
            intentBuilder()
        }

    fun start(context: Context, options: IntentOptions, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, options, intentBuilder))
    }

    val Activity.intentOptions: IntentOptions?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.intent.getParcelableExtra<IntentOptions>(INTENT_OPTIONS_KEY, optionsClass.java)
        } else {
            @Suppress("DEPRECATION")
            this.intent.getParcelableExtra<IntentOptions>(INTENT_OPTIONS_KEY)
        }
}