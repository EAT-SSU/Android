package com.eatssu.android.presentation.widget.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.material3.ColorProviders

object EATSSUWidgetColorScheme {

    private val LightColorScheme = lightColorScheme(
        primary = Color(0xFF1F1F1F), // 텍스트 기본
        onPrimary = Color(0xFFFFFFFF), // 메인 배경
        onBackground = Color(0xFFFAFAFB) // 전체 배경
    )

    val colors = ColorProviders(
        light = LightColorScheme,
        dark = LightColorScheme
    )
}