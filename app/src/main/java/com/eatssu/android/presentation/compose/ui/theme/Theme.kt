package com.aurora.carevision.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Color 설정
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    secondary = Secondary,
    onSecondary = Black,
    background = White,
    surface = White,
    onSurface = Black,
    error = Error
)

// Typography 설정
private val LocalEatssuTypography = staticCompositionLocalOf<EatssuTypography> {
    error("No EatssuTypography provided")
}

object EatssuTheme {
    val typography: EatssuTypography
        @Composable get() = LocalEatssuTypography.current
}

@Composable
fun ProvideEatssuTypography(typography: EatssuTypography, content: @Composable () -> Unit) {
    val provideTypography = remember { typography.copy() }
    CompositionLocalProvider(
        LocalEatssuTypography provides provideTypography,
        content = content
    )
}

@Composable
fun EatssuTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme
    val typography = careVisionTypography()

    // set status bar & navigation bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = White.toArgb()
            window.navigationBarColor = White.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = true
        }
    }

    ProvideEatssuTypography(typography) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(
                displayLarge = typography.headingPrimary,
                displayMedium = typography.headingSecondary,
                headlineLarge = typography.subtitle1,
                headlineMedium = typography.subtitle2,
                bodyLarge = typography.body1,
                bodyMedium = typography.body2,
                bodySmall = typography.body3,
                labelLarge = typography.caption1,
                labelMedium = typography.caption2,
                labelSmall = typography.button1
            ),
            content = content
        )
    }
}