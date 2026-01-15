package com.practicum.playlistmaker.creater

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)

        val settingRepository = Creator.getSettingsRepository()
        val currentTheme = settingRepository.getThemeSettings()

        AppCompatDelegate.setDefaultNightMode(
            if (currentTheme.isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}