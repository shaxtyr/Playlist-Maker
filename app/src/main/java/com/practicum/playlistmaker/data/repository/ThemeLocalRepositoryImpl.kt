package com.practicum.playlistmaker.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.repository.ThemeLocalRepository

class ThemeLocalRepositoryImpl(context: Context) : ThemeLocalRepository {
    val themePreferences = context.getSharedPreferences(THEME_APP_PREFERENCES, MODE_PRIVATE)

    override fun getTheme(): Boolean {
        return themePreferences.getBoolean(THEME_PREFS_KEY, false)
    }

    override fun applyTheme() {
        val isDarkTheme = getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun saveTheme(theme: Boolean) {
        themePreferences.edit()
            .putBoolean(THEME_PREFS_KEY, theme)
            .apply()
    }

    companion object {
        private const val THEME_APP_PREFERENCES = "theme_app_preferences"
        private const val THEME_PREFS_KEY = "dark_theme"
    }
}