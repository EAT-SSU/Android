package com.eatssu.android.data.local

import android.content.Context
import android.content.SharedPreferences
import com.eatssu.android.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class AppThemePreferences @Inject constructor(
    @ApplicationContext context: Context,
) {

    companion object {
        private const val PREFS_NAME = "app_theme"
        private const val KEY_APP_THEME = "app_theme"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAppTheme(): AppTheme {
        return AppTheme.fromStringOrDefault(prefs.getString(KEY_APP_THEME, null).orEmpty())
    }

    fun setAppTheme(theme: AppTheme) {
        prefs.edit {
            putString(KEY_APP_THEME, theme.remoteValue)
        }
    }
}
