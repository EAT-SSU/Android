package com.eatssu.android.test

import io.kotest.core.spec.style.BehaviorSpec

abstract class AppBehaviorSpec(
    body: BehaviorSpec.() -> Unit,
) : BehaviorSpec(body) {
    init {
        listener(MainDispatcherListener())
    }
}
