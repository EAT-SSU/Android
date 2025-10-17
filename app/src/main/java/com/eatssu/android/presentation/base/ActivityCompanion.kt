package com.eatssu.android.presentation.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.IntentCompat.getParcelableExtra
import kotlin.reflect.KClass

private const val INTENT_ARGS_KEY = "intent_args"

abstract class ActivityCompanion(
    protected val activityClass: KClass<out Activity>,
) {
    fun intent(
        context: Context, intentBuilder: Intent.() -> Unit = {}
    ): Intent = Intent(context, activityClass.java).apply {
        intentBuilder()
    }

    fun start(context: Context, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, intentBuilder))
    }

}

abstract class ActivityCompanionWithArgs<TArgs>(
    protected val activityClass: KClass<out Activity>,
    protected val argsClass: KClass<TArgs>,
) where TArgs : Parcelable {
    fun intent(
        context: Context, args: TArgs, intentBuilder: Intent.() -> Unit = {}
    ): Intent = Intent(context, activityClass.java).apply {
        putExtra(INTENT_ARGS_KEY, args)
        intentBuilder()
    }

    fun start(context: Context, args: TArgs, intentBuilder: Intent.() -> Unit = {}) {
        context.startActivity(intent(context, args, intentBuilder))
    }

    val Activity.intentOptions: TArgs?
        get() = getParcelableExtra(
            this.intent, INTENT_ARGS_KEY, argsClass.java
        )
}

abstract class ActivityCompanionWithArgsDefault<TArgs>(
    activityClass: KClass<out Activity>,
    argsClass: KClass<TArgs>,
    private val defaultArgs: (Context) -> TArgs,
) : ActivityCompanionWithArgs<TArgs>(activityClass, argsClass) where TArgs : Parcelable {

    fun intent(
        context: Context, intentBuilder: Intent.() -> Unit = {}
    ): Intent = Intent(context, activityClass.java).apply {
        putExtra(INTENT_ARGS_KEY, defaultArgs(context))
        intentBuilder()
    }

}
