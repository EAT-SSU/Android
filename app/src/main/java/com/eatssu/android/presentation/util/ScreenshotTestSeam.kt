package com.eatssu.android.presentation.util

/**
 * Screenshot regression tests toggle deterministic rendering through this seam.
 * It is disabled by default and must be explicitly enabled in test code.
 */
object ScreenshotTestSeam {
    private const val PROPERTY_KEY = "eatssu.screenshot.test"

    @Volatile
    private var forceEnabled: Boolean = false

    val isEnabled: Boolean
        get() = forceEnabled || System.getProperty(PROPERTY_KEY) == "true"

    fun enableForTest() {
        forceEnabled = true
        System.setProperty(PROPERTY_KEY, "true")
    }

    fun disableForTest() {
        forceEnabled = false
        System.clearProperty(PROPERTY_KEY)
    }
}
