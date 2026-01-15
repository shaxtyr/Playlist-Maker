package com.practicum.playlistmaker.setting.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.setting.domain.model.ThemeSettings
import com.practicum.playlistmaker.setting.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    val themePreferences = context.getSharedPreferences(THEME_APP_PREFERENCES, MODE_PRIVATE)

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(themePreferences.getBoolean(THEME_PREFS_KEY, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        themePreferences.edit()
            .putBoolean(THEME_PREFS_KEY, settings.isDarkTheme)
            .apply()
    }

    companion object {
        private const val THEME_APP_PREFERENCES = "theme_app_preferences"
        private const val THEME_PREFS_KEY = "dark_theme"
    }
}