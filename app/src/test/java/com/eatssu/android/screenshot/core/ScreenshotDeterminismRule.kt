package com.eatssu.android.screenshot.core

import android.provider.Settings
import com.eatssu.android.presentation.util.ScreenshotTestSeam
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.robolectric.RuntimeEnvironment
import java.util.Locale
import java.util.TimeZone

class ScreenshotDeterminismRule : TestWatcher() {
    private var previousLocale: Locale = Locale.getDefault()
    private var previousTimezone: TimeZone = TimeZone.getDefault()
    private var previousAnimatorScale: Float? = null
    private var previousTransitionScale: Float? = null
    private var previousWindowScale: Float? = null

    override fun starting(description: Description) {
        previousLocale = Locale.getDefault()
        previousTimezone = TimeZone.getDefault()
        previousAnimatorScale = readAnimationScale(Settings.Global.ANIMATOR_DURATION_SCALE)
        previousTransitionScale = readAnimationScale(Settings.Global.TRANSITION_ANIMATION_SCALE)
        previousWindowScale = readAnimationScale(Settings.Global.WINDOW_ANIMATION_SCALE)

        Locale.setDefault(Locale.KOREA)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
        System.setProperty("user.language", "ko")
        System.setProperty("user.country", "KR")
        System.setProperty("user.timezone", "Asia/Seoul")
        System.setProperty("eatssu.screenshot.test", "true")
        disableAnimations()

        ScreenshotTestSeam.enableForTest()
    }

    override fun finished(description: Description) {
        Locale.setDefault(previousLocale)
        TimeZone.setDefault(previousTimezone)
        System.clearProperty("user.language")
        System.clearProperty("user.country")
        System.clearProperty("user.timezone")
        System.clearProperty("eatssu.screenshot.test")

        restoreAnimations()

        ScreenshotTestSeam.disableForTest()
    }

    private fun disableAnimations() {
        writeAnimationScale(Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        writeAnimationScale(Settings.Global.TRANSITION_ANIMATION_SCALE, 0f)
        writeAnimationScale(Settings.Global.WINDOW_ANIMATION_SCALE, 0f)
    }

    private fun restoreAnimations() {
        writeAnimationScale(Settings.Global.ANIMATOR_DURATION_SCALE, previousAnimatorScale ?: 1f)
        writeAnimationScale(Settings.Global.TRANSITION_ANIMATION_SCALE, previousTransitionScale ?: 1f)
        writeAnimationScale(Settings.Global.WINDOW_ANIMATION_SCALE, previousWindowScale ?: 1f)
    }

    private fun readAnimationScale(setting: String): Float? {
        val resolver = RuntimeEnvironment.getApplication().contentResolver
        return runCatching { Settings.Global.getFloat(resolver, setting) }.getOrNull()
    }

    private fun writeAnimationScale(setting: String, value: Float) {
        val resolver = RuntimeEnvironment.getApplication().contentResolver
        runCatching { Settings.Global.putFloat(resolver, setting, value) }
    }
}
