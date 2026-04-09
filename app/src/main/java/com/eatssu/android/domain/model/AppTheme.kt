package com.eatssu.android.domain.model

import androidx.annotation.AnyRes
import androidx.annotation.DrawableRes
import com.eatssu.android.R

enum class AppTheme(
    val remoteValue: String,
    @AnyRes val splashBackgroundResId: Int,
    @DrawableRes val splashLogoResId: Int,
    val launcherAliasSuffix: String,
) {
    DEFAULT(
        remoteValue = "default",
        splashBackgroundResId = R.color.primary,
        splashLogoResId = R.drawable.img_logo,
        launcherAliasSuffix = ".alias.DefaultLauncherAlias",
    ),
    CHRISTMAS(
        remoteValue = "christmas",
        splashBackgroundResId = R.drawable.img_background_snow,
        splashLogoResId = R.drawable.img_logo_snow,
        launcherAliasSuffix = ".alias.ChristmasLauncherAlias",
    ),
    SPRING(
        remoteValue = "spring",
        splashBackgroundResId = R.drawable.img_background_spring,
        splashLogoResId = R.drawable.img_logo,
        launcherAliasSuffix = ".alias.SpringLauncherAlias",
    );

    companion object {
        fun fromStringOrDefault(value: String): AppTheme {
            return entries.find { it.remoteValue.equals(value, ignoreCase = true) } ?: DEFAULT
        }
    }
}
