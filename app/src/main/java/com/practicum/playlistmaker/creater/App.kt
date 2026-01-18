package com.practicum.playlistmaker.creater

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)

        val settingsInteractor = Creator.provideSettingsInteractor()
        val currentTheme = settingsInteractor.getThemeSettings()

        settingsInteractor.updateThemeSetting(currentTheme)

    }
}