package com.eatssu.android.presentation.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.IntentCompat.getParcelableExtra
import kotlin.reflect.KClass

abstract class ActivityCompanion(
    private val activityClass: KClass<out Activity>,
) {
    fun intent(
        context: Context,
        intentBuilder: Intent.() -> Unit = {}
    ): Intent =
        Intent(context, activityClass.java).apply {
            intentBuilder()
        }

    fun start(context: Context, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, intentBuilder))
    }

}

abstract class ActivityCompanionWithArgs<TArgs>(
    private val activityClass: KClass<out Activity>,
    private val argsClass: KClass<TArgs>
) where TArgs : Parcelable {
    private companion object {
        private const val INTENT_ARGS_KEY = "intent_args"
    }

    fun intent(
        context: Context,
        args: TArgs,
        intentBuilder: Intent.() -> Unit = {}
    ): Intent =
        Intent(context, activityClass.java).apply {
            putExtra(INTENT_ARGS_KEY, args)
            intentBuilder()
        }

    fun start(context: Context, args: TArgs, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, args, intentBuilder))
    }

    val Activity.intentOptions: TArgs?
        get() = getParcelableExtra(
            this.intent,
            INTENT_ARGS_KEY,
            argsClass.java
        )
}
