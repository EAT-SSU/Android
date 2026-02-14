package com.eatssu.android.screenshot.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.github.takahirom.roborazzi.captureRoboImage

private const val DEFAULT_WIDTH_PX = 1080
private const val DEFAULT_HEIGHT_PX = 2400

object ScreenshotCapture {
    fun captureView(
        type: String,
        target: String,
        state: String,
        view: View,
        widthPx: Int = DEFAULT_WIDTH_PX,
        heightPx: Int = DEFAULT_HEIGHT_PX,
    ) {
        if (!shouldCapture()) return

        view.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, EXACTLY),
        )
        view.layout(0, 0, widthPx, heightPx)

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        bitmap.captureRoboImage(filePath(type, target, state))
    }

    fun captureComposeRoot(
        type: String,
        target: String,
        state: String,
        composeRule: ComposeContentTestRule,
    ) {
        if (!shouldCapture()) return
        composeRule.onRoot().captureRoboImage(filePath(type, target, state))
    }

    private fun filePath(type: String, target: String, state: String): String {
        return "$type/$target/$state.png"
    }

    private fun shouldCapture(): Boolean {
        return System.getProperty("eatssu.screenshot.capture") == "true"
    }
}
