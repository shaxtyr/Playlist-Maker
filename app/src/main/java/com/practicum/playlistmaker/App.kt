package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creater.Creator

class App: Application() {

    var darkTheme = false
    private set

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        val sharedPrefs = getSharedPreferences(THEME_APP_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_APP_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        getSharedPreferences(THEME_APP_PREFERENCES, MODE_PRIVATE).edit()
            .putBoolean(THEME_APP_KEY, darkThemeEnabled)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        private const val THEME_APP_PREFERENCES = "theme_app_preferences"
        private const val THEME_APP_KEY = "dark_theme"
    }
}